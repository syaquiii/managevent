package com.managevent.app

import com.managevent.app.auth.data.AuthRepository
import com.managevent.app.auth.data.RegisterResult
import com.managevent.app.auth.data.UserModel
import com.managevent.app.auth.viewmodel.AuthViewModel
import com.managevent.app.core.util.UiState
import com.managevent.app.features.agenda.data.AgendaModel
import com.managevent.app.features.agenda.data.AgendaRepository
import com.managevent.app.features.agenda.data.BookedEventModel
import com.managevent.app.features.agenda.viewmodel.AgendaViewModel
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.data.KatalogRepository
import com.managevent.app.features.katalog.viewmodel.KatalogSortOption
import com.managevent.app.features.katalog.viewmodel.KatalogViewModel
import com.managevent.app.features.pemesanan.data.PemesananRepository
import com.managevent.app.features.pemesanan.data.TransaksiModel
import com.managevent.app.features.pemesanan.viewmodel.PemesananViewModel
import com.managevent.app.features.ulasan.data.UlasanModel
import com.managevent.app.features.ulasan.data.UlasanRepository
import com.managevent.app.features.ulasan.viewmodel.UlasanViewModel
import com.managevent.app.features.wishlist.data.WishlistRepository
import com.managevent.app.features.wishlist.viewmodel.WishlistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun authViewModel_loginRegisterAndLogoutFlow() = runTest {
        val sampleUser = UserModel(id = "uid-1", email = "a@b.com", fullName = "Alice", role = "VENDOR")
        val fakeAuthRepo = object : AuthRepository() {
            override suspend fun fetchCurrentProfile(): UserModel? = null
            override suspend fun login(emailInput: String, passwordInput: String) = UiState.Success(sampleUser)
            override suspend fun register(
                emailInput: String,
                passwordInput: String,
                fullName: String,
                role: String
            ) = UiState.Success(RegisterResult(sampleUser.copy(fullName = fullName, role = role), requiresEmailConfirmation = true))
            override suspend fun updateProfile(userId: String, fullName: String, role: String) =
                UiState.Success(sampleUser.copy(fullName = fullName, role = role))
            override suspend fun logout() {}
        }

        val vm = AuthViewModel(fakeAuthRepo)
        advanceUntilIdle()
        assertNull(vm.currentUser.value)

        vm.login("a@b.com", "123456")
        advanceUntilIdle()
        assertEquals(sampleUser, vm.currentUser.value)

        vm.updateProfile("Alice Updated", "PEMESAN")
        advanceUntilIdle()
        assertEquals("Alice Updated", vm.currentUser.value?.fullName)
        assertTrue(vm.actionState.value is UiState.Success)

        vm.logout()
        advanceUntilIdle()
        assertNull(vm.currentUser.value)
        assertEquals(UiState.Idle, vm.loginState.value)

        vm.register("new@b.com", "123456", "Bob", "VENDOR")
        advanceUntilIdle()
        assertTrue(vm.registerState.value is UiState.Success)
        // Requires email confirmation should NOT set active currentUser yet
        assertNull(vm.currentUser.value)
    }

    @Test
    fun katalogViewModel_filteringSortingFavoriteRollbackAndSubmit() = runTest {
        val events = listOf(
            EventModel(id = "e1", title = "Jazz Night", category = "Konser", location = "Istora Senayan, Jakarta", price = 350000.0),
            EventModel(id = "e2", title = "Wedding Expo", category = "Pernikahan", location = "Jalan Braga, Bandung", price = 100000.0)
        )
        var shouldFailWishlist = true
        var shouldFailUpload = true

        val fakeKatalogRepo = object : KatalogRepository() {
            override suspend fun getEvents() = UiState.Success(events)
            override suspend fun uploadPoster(fileName: String, byteArray: ByteArray) =
                if (shouldFailUpload) UiState.Error("Upload gagal") else UiState.Success("https://cdn/poster.jpg")
            override suspend fun addEvent(event: EventModel) =
                UiState.Success(event.copy(id = "uuid-server-1"))
        }
        val fakeWishlistRepo = object : WishlistRepository() {
            override suspend fun getWishlistEvents(userIdInput: String) = UiState.Success(listOf(events[0]))
            override suspend fun addToWishlist(userIdInput: String, eventId: String) =
                if (shouldFailWishlist) UiState.Error("Gagal simpan") else UiState.Success("Ditambahkan ke favorit")
        }
        val fakeUlasanRepo = object : UlasanRepository() {
            override suspend fun getUlasanByEvent(eventId: String) = UiState.Success(emptyList<UlasanModel>())
        }

        val vm = KatalogViewModel(fakeKatalogRepo, fakeWishlistRepo, fakeUlasanRepo, autoFetch = false)
        vm.fetchEvents("uid-1")
        advanceUntilIdle()

        val initialList = (vm.eventsState.value as UiState.Success).data
        assertEquals(2, initialList.size)
        assertTrue(initialList.first { it.id == "e1" }.isFavorite)
        assertFalse(initialList.first { it.id == "e2" }.isFavorite)

        // Filter by location and sort ascending
        vm.onLocationSelected("Bandung")
        assertEquals(listOf("e2"), (vm.eventsState.value as UiState.Success).data.map { it.id })

        vm.resetFilters()
        vm.onSortOptionSelected(KatalogSortOption.HARGA_TERENDAH)
        assertEquals(listOf("e2", "e1"), (vm.eventsState.value as UiState.Success).data.map { it.id })

        // Guest favorite blocked
        var feedback = ""
        vm.toggleFavorite("e2", "") { feedback = it }
        assertTrue(feedback.contains("masuk", ignoreCase = true))

        // Server error rolls back favorite state
        vm.toggleFavorite("e2", "uid-1") { feedback = it }
        advanceUntilIdle()
        assertFalse((vm.eventsState.value as UiState.Success).data.first { it.id == "e2" }.isFavorite)
        assertEquals("Gagal simpan", feedback)

        // Upload failure prevents addEvent
        vm.submitEvent(null, EventModel(title = "New Event", price = 50000.0), byteArrayOf(1, 2, 3))
        advanceUntilIdle()
        assertTrue(vm.formState.value is UiState.Error)

        // Upload success returns server UUID event
        shouldFailUpload = false
        var completed = false
        vm.submitEvent(null, EventModel(title = "New Event", price = 50000.0), byteArrayOf(1, 2, 3)) { completed = true }
        advanceUntilIdle()
        assertTrue(completed)
        assertTrue((vm.eventsState.value as UiState.Success).data.any { it.id == "uuid-server-1" })
    }

    @Test
    fun pemesananViewModel_checkoutClampPayAndCancelErrorHandling() = runTest {
        var cancelShouldFail = true
        val sampleOrder = TransaksiModel(
            id = "trx-1",
            eventId = "e1",
            jumlahTiket = 10,
            totalHarga = 1000000.0,
            status = "MENUNGGU_PEMBAYARAN",
            statusPembayaran = "PENDING"
        )

        val fakeRepo = object : PemesananRepository() {
            override suspend fun checkout(transaksi: TransaksiModel) =
                UiState.Success(transaksi.copy(id = "trx-1"))
            override suspend fun processPayment(transaksiId: String, bankCode: String) =
                UiState.Success(sampleOrder.copy(status = "BERJALAN", statusPembayaran = "BERHASIL", metodePembayaran = bankCode))
            override suspend fun getRiwayatPemesanan() =
                UiState.Success(listOf(sampleOrder.copy(status = "BERJALAN", statusPembayaran = "BERHASIL")))
            override suspend fun batalkanPemesanan(transaksiId: String) =
                if (cancelShouldFail) UiState.Error("Gagal batalkan") else UiState.Success("OK")
        }

        val vm = PemesananViewModel(fakeRepo, paymentStepDelayMs = 0L)
        vm.submitCheckout("e1", jumlahTiket = 99, catatan = "  kursi depan  ", pricePerTicket = 100000.0)
        advanceUntilIdle()

        val created = (vm.checkoutState.value as UiState.Success).data
        assertEquals(10, created.jumlahTiket)
        assertEquals(1000000.0, created.totalHarga, 0.01)

        vm.payNow("trx-1", "bri_va")
        advanceUntilIdle()
        assertEquals("BERHASIL", (vm.detailState.value as UiState.Success).data.statusPembayaran)

        // Failed cancellation must NOT mutate detailState to DIBATALKAN
        var errorMsg = ""
        vm.cancelPemesanan("trx-1", onError = { errorMsg = it })
        advanceUntilIdle()
        assertEquals("Gagal batalkan", errorMsg)
        assertEquals("BERJALAN", (vm.detailState.value as UiState.Success).data.status)

        cancelShouldFail = false
        var cancelCompleted = false
        vm.cancelPemesanan("trx-1", onComplete = { cancelCompleted = true })
        advanceUntilIdle()
        assertTrue(cancelCompleted)
        assertEquals("DIBATALKAN", (vm.detailState.value as UiState.Success).data.status)
    }

    @Test
    fun ulasanViewModel_computesRealRatingSummaryAndValidatesInput() = runTest {
        val reviews = listOf(
            UlasanModel(id = "u1", eventId = "e1", userId = "usr-1", username = "A", rating = 5, comment = "Bagus"),
            UlasanModel(id = "u2", eventId = "e1", userId = "usr-2", username = "B", rating = 3, comment = "Cukup")
        )
        val fakeUlasanRepo = object : UlasanRepository() {
            override suspend fun getUlasanByEvent(eventId: String) = UiState.Success(reviews)
        }
        val fakeKatalogRepo = object : KatalogRepository() {
            override suspend fun getEventById(id: String) = UiState.Success(EventModel(id = id, title = "Konser Akbar"))
        }

        val vm = UlasanViewModel(fakeUlasanRepo, fakeKatalogRepo)
        vm.fetchUlasan("e1")
        advanceUntilIdle()

        assertEquals("Konser Akbar", vm.eventTitle.value)
        assertEquals(4.0, vm.ratingSummary.value.averageRating, 0.01)
        assertEquals(2, vm.ratingSummary.value.totalCount)
        assertEquals(1, vm.ratingSummary.value.countsByStar[5])
        assertEquals(1, vm.ratingSummary.value.countsByStar[3])

        vm.onFilterChanged(5)
        assertEquals(1, (vm.ulasanState.value as UiState.Success).data.size)

        // Blank comment validation
        vm.onCommentChange("   ")
        vm.submitUlasan("e1", "usr-1", "User", onSuccess = {}, onError = {})
        assertEquals("Komentar ulasan wajib diisi", vm.commentError.value)
    }

    @Test
    fun wishlistAndAgendaViewModels_handleFiltersValidationAndErrors() = runTest {
        val favEvents = listOf(
            EventModel(id = "e1", title = "Seminar AI", category = "Seminar"),
            EventModel(id = "e2", title = "Festival Kopi", category = "Festival")
        )
        val fakeWishlistRepo = object : WishlistRepository() {
            override suspend fun getWishlistEvents(userIdInput: String) = UiState.Success(favEvents)
            override suspend fun removeFromWishlist(userIdInput: String, eventId: String) = UiState.Error("Server error")
        }
        val fakeKatalogRepo = object : KatalogRepository() {
            override suspend fun getEventById(id: String) = UiState.Success(favEvents.first())
        }

        val wishlistVm = WishlistViewModel(fakeWishlistRepo, fakeKatalogRepo)
        wishlistVm.fetchWishlist("uid-1")
        advanceUntilIdle()
        assertEquals(2, wishlistVm.totalCount.value)
        wishlistVm.onCategorySelected("Seminar")
        assertEquals(listOf("e1"), (wishlistVm.wishlistState.value as UiState.Success).data.map { it.id })

        // Removal failure restores list
        var err = ""
        wishlistVm.removeFromWishlist("e1", "uid-1", onError = { err = it })
        advanceUntilIdle()
        assertEquals("Server error", err)
        assertEquals(2, wishlistVm.totalCount.value)

        // AgendaViewModel validation & error surfacing
        val fakeAgendaRepo = object : AgendaRepository() {
            override suspend fun getBookedEvents() = UiState.Success(
                listOf(BookedEventModel("e1", "Seminar AI", "2026-12-01", "Jakarta", null, "Berjalan", 0))
            )
            override suspend fun getNotes(eventId: String) = UiState.Success(emptyList<AgendaModel>())
            override suspend fun addNote(note: AgendaModel) = UiState.Error("Gagal simpan ke server")
        }
        val agendaVm = AgendaViewModel(fakeAgendaRepo)
        val collectedMessages = mutableListOf<String>()
        val collectJob = launch { collectedMessages.add(agendaVm.message.first()) }

        agendaVm.startAdd("e1")
        agendaVm.save("e1") {}
        assertEquals("Judul wajib diisi", agendaVm.titleError.value)
        assertEquals("Target selesai wajib diisi", agendaVm.dateError.value)

        agendaVm.onTitleChange("Siapkan tiket")
        agendaVm.onDateChange("2026-11-30")
        var saveCalled = false
        agendaVm.save("e1") { saveCalled = true }
        advanceUntilIdle()

        assertFalse(saveCalled)
        assertEquals(listOf("Gagal simpan ke server"), collectedMessages)
        collectJob.cancel()
    }
}
