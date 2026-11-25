package com.managevent.app.core.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.managevent.app.auth.ui.LoginScreen
import com.managevent.app.auth.ui.OnboardingScreen
import com.managevent.app.auth.ui.ProfileScreen
import com.managevent.app.auth.ui.RegisterScreen
import com.managevent.app.auth.viewmodel.AuthViewModel
import com.managevent.app.core.util.UiState
import com.managevent.app.features.agenda.ui.AgendaFormScreen
import com.managevent.app.features.agenda.ui.AgendaListScreen
import com.managevent.app.features.agenda.ui.BookedEventListScreen
import com.managevent.app.features.agenda.viewmodel.AgendaViewModel
import com.managevent.app.features.katalog.ui.KatalogDetailScreen
import com.managevent.app.features.katalog.ui.KatalogFormScreen
import com.managevent.app.features.katalog.ui.KatalogListScreen
import com.managevent.app.features.katalog.viewmodel.KatalogViewModel
import com.managevent.app.features.pemesanan.ui.CheckoutScreen
import com.managevent.app.features.pemesanan.ui.DetailPemesananScreen
import com.managevent.app.features.pemesanan.ui.PaymentScreen
import com.managevent.app.features.pemesanan.ui.RiwayatPemesananScreen
import com.managevent.app.features.pemesanan.viewmodel.PemesananViewModel
import com.managevent.app.features.ulasan.ui.UlasanFormScreen
import com.managevent.app.features.ulasan.ui.UlasanListScreen
import com.managevent.app.features.ulasan.viewmodel.UlasanViewModel
import com.managevent.app.features.wishlist.ui.WishlistDetailScreen
import com.managevent.app.features.wishlist.ui.WishlistScreen
import com.managevent.app.features.wishlist.viewmodel.WishlistViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.KatalogList.route,
    onOnboardingCompleted: () -> Unit = {}
) {
    val authViewModel: AuthViewModel = viewModel()
    val katalogViewModel: KatalogViewModel = viewModel()
    val pemesananViewModel: PemesananViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()
    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()
    val authActionState by authViewModel.actionState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(currentUser?.id) {
        katalogViewModel.fetchEvents(currentUser?.id.orEmpty())
        if (currentUser == null) {
            pemesananViewModel.clearSessionData()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Routes
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onSkipClick = {
                    onOnboardingCompleted()
                    navController.navigate(Screen.KatalogList.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLoginClick = {
                    onOnboardingCompleted()
                    navController.navigate(Screen.Login.route)
                },
                onFinishOnboarding = {
                    onOnboardingCompleted()
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Login.route) {
            LaunchedEffect(Unit) { authViewModel.resetAuthState() }
            val canGoBack = navController.previousBackStackEntry != null
            LoginScreen(
                loginState = loginState,
                resetPasswordState = authActionState,
                onLoginClick = { email, password ->
                    authViewModel.login(
                        email = email,
                        pass = password,
                        onSuccess = {
                            onOnboardingCompleted()
                            navController.navigate(Screen.KatalogList.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                },
                onForgotPasswordClick = authViewModel::sendPasswordReset,
                onClearResetState = authViewModel::clearActionState,
                onNavigateToRegister = {
                    authViewModel.resetAuthState()
                    navController.navigate(Screen.Register.route)
                },
                onBackClick = if (canGoBack) { { navController.popBackStack() } } else null
            )
        }
        composable(Screen.Register.route) {
            LaunchedEffect(Unit) { authViewModel.resetAuthState() }
            RegisterScreen(
                registerState = registerState,
                onRegisterClick = { email, password, fullName, role ->
                    authViewModel.register(
                        email = email,
                        pass = password,
                        fullName = fullName,
                        role = role
                    )
                },
                onRegistrationComplete = { requiresEmailConfirmation ->
                    authViewModel.resetAuthState()
                    onOnboardingCompleted()
                    if (requiresEmailConfirmation) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.KatalogList.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavigateToLogin = {
                    authViewModel.resetAuthState()
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Profile.route) {
            LaunchedEffect(Unit) { authViewModel.checkCurrentSession() }
            ProfileScreen(
                user = currentUser,
                actionState = authActionState,
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLogoutClick = {
                    authViewModel.logout {
                        pemesananViewModel.clearSessionData()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavigateToRiwayat = { navController.navigate(Screen.RiwayatPemesanan.route) },
                onNavigateToWishlist = { navController.navigate(Screen.Wishlist.route) },
                onUpdateProfile = { fullName, role -> authViewModel.updateProfile(fullName, role) },
                onUpdatePassword = { newPassword -> authViewModel.updatePassword(newPassword) },
                onClearActionState = authViewModel::clearActionState
            )
        }

        // Feature 1: Katalog
        composable(Screen.KatalogList.route) {
            val eventsState by katalogViewModel.eventsState.collectAsStateWithLifecycle()
            val searchQuery by katalogViewModel.searchQuery.collectAsStateWithLifecycle()
            val selectedCategory by katalogViewModel.selectedCategory.collectAsStateWithLifecycle()
            val selectedLocation by katalogViewModel.selectedLocation.collectAsStateWithLifecycle()
            val availableLocations by katalogViewModel.availableLocations.collectAsStateWithLifecycle()
            val sortOption by katalogViewModel.sortOption.collectAsStateWithLifecycle()

            KatalogListScreen(
                eventsState = eventsState,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                selectedLocation = selectedLocation,
                availableLocations = availableLocations,
                sortOption = sortOption,
                userRole = currentUser?.role ?: "PEMESAN",
                userName = currentUser?.fullName.orEmpty(),
                onEventClick = { eventId -> navController.navigate(Screen.KatalogDetail.createRoute(eventId)) },
                onFavoriteToggle = { eventId ->
                    if (currentUser == null) {
                        Toast.makeText(context, "Silakan masuk untuk menyimpan favorit", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    } else {
                        katalogViewModel.toggleFavorite(eventId, currentUser?.id.orEmpty()) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onSearchQueryChange = katalogViewModel::onSearchQueryChange,
                onCategorySelected = katalogViewModel::onCategorySelected,
                onLocationSelected = katalogViewModel::onLocationSelected,
                onSortOptionSelected = katalogViewModel::onSortOptionSelected,
                onResetFilters = katalogViewModel::resetFilters,
                onRetry = { katalogViewModel.fetchEvents(currentUser?.id.orEmpty()) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onAddEventClick = {
                    katalogViewModel.resetFormState()
                    navController.navigate(Screen.KatalogForm.createRoute())
                }
            )
        }
        composable(Screen.KatalogDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val detailState by katalogViewModel.eventDetailState.collectAsStateWithLifecycle()
            val reviewSummary by katalogViewModel.reviewSummary.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                if (eventId.isNotBlank()) katalogViewModel.fetchEventById(eventId)
            }

            KatalogDetailScreen(
                detailState = detailState,
                reviewSummary = reviewSummary,
                userRole = currentUser?.role ?: "PEMESAN",
                currentUserId = currentUser?.id ?: "",
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    katalogViewModel.resetFormState()
                    navController.navigate(Screen.KatalogForm.createRoute(id))
                },
                onDeleteConfirm = { id ->
                    katalogViewModel.deleteEvent(
                        id = id,
                        onComplete = {
                            Toast.makeText(context, "Event berhasil dihapus", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onFavoriteToggle = {
                    if (currentUser == null) {
                        Toast.makeText(context, "Silakan masuk untuk menyimpan favorit", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    } else {
                        katalogViewModel.toggleFavorite(eventId, currentUser?.id.orEmpty()) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onBookClick = { id ->
                    if (currentUser == null) {
                        Toast.makeText(context, "Harap masuk (login) terlebih dahulu", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    } else {
                        navController.navigate(Screen.Checkout.createRoute(id))
                    }
                },
                onReviewClick = { id -> navController.navigate(Screen.UlasanList.createRoute(id)) }
            )
        }
        composable(
            route = Screen.KatalogForm.route,
            arguments = listOf(navArgument("eventId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            val detailState by katalogViewModel.eventDetailState.collectAsStateWithLifecycle()
            val formState by katalogViewModel.formState.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                katalogViewModel.resetFormState()
                if (!eventId.isNullOrBlank()) {
                    katalogViewModel.fetchEventById(eventId)
                }
            }

            val initialEvent = eventId?.let { id ->
                (detailState as? UiState.Success)?.data?.takeIf { it.id == id }
            }

            KatalogFormScreen(
                initialEvent = initialEvent,
                formState = formState,
                onBackClick = { navController.popBackStack() },
                onSubmitEvent = { eventInput, imageBytes ->
                    katalogViewModel.submitEvent(
                        existingEventId = eventId?.takeIf { it.isNotBlank() },
                        eventInput = eventInput,
                        selectedImageBytes = imageBytes,
                        onComplete = {
                            Toast.makeText(
                                context,
                                if (!eventId.isNullOrBlank()) "Event berhasil diperbarui" else "Event berhasil ditambahkan",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                    )
                }
            )
        }

        // Feature 2: Pemesanan
        composable(Screen.Checkout.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            val checkoutState by pemesananViewModel.checkoutState.collectAsStateWithLifecycle()
            val eventState by katalogViewModel.eventDetailState.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                if (eventId.isNotBlank()) katalogViewModel.fetchEventById(eventId)
            }
            LaunchedEffect(checkoutState) {
                if (checkoutState is UiState.Success) {
                    val created = (checkoutState as UiState.Success).data
                    pemesananViewModel.consumeCheckoutSuccess()
                    navController.navigate(Screen.Payment.createRoute(created.id)) {
                        popUpTo(Screen.KatalogList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }

            CheckoutScreen(
                eventId = eventId,
                eventState = eventState,
                checkoutState = checkoutState,
                onBackClick = { navController.popBackStack() },
                onSubmitCheckout = { jumlahTiket, catatan, event ->
                    pemesananViewModel.submitCheckout(
                        eventId = event.id,
                        jumlahTiket = jumlahTiket,
                        catatan = catatan,
                        pricePerTicket = event.price,
                        eventTitle = event.title,
                        eventDate = event.date,
                        eventImageUrl = event.imageUrl
                    )
                }
            )
        }
        composable(Screen.Payment.route) { backStackEntry ->
            val transaksiId = backStackEntry.arguments?.getString("transaksiId").orEmpty()
            val detailState by pemesananViewModel.detailState.collectAsStateWithLifecycle()
            val paymentState by pemesananViewModel.paymentState.collectAsStateWithLifecycle()
            val loadingMessage by pemesananViewModel.paymentLoadingMessage.collectAsStateWithLifecycle()

            LaunchedEffect(transaksiId) {
                if (transaksiId.isNotBlank()) {
                    pemesananViewModel.fetchDetailById(transaksiId)
                }
            }

            PaymentScreen(
                detailState = detailState,
                paymentState = paymentState,
                paymentLoadingMessage = loadingMessage,
                onBackClick = {
                    pemesananViewModel.consumePaymentState()
                    navController.popBackStack()
                },
                onPayClick = { bankCode -> pemesananViewModel.payNow(transaksiId, bankCode) },
                onRenewClick = { bankCode -> pemesananViewModel.renewPayment(transaksiId, bankCode) },
                onCancelClick = {
                    pemesananViewModel.cancelPemesanan(
                        transaksiId = transaksiId,
                        onComplete = {
                            Toast.makeText(context, "Pembayaran dibatalkan", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onNavigateToDetail = { id ->
                    pemesananViewModel.consumePaymentState()
                    navController.navigate(Screen.DetailPemesanan.createRoute(id)) {
                        popUpTo(Screen.Payment.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.RiwayatPemesanan.route) {
            val riwayatState by pemesananViewModel.riwayatState.collectAsStateWithLifecycle()
            LaunchedEffect(currentUser?.id) { pemesananViewModel.fetchRiwayat() }

            RiwayatPemesananScreen(
                riwayatState = riwayatState,
                onBackClick = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.KatalogList.route) {
                            popUpTo(Screen.KatalogList.route) { inclusive = false }
                        }
                    }
                },
                onTransactionClick = { pemesananId ->
                    navController.navigate(Screen.DetailPemesanan.createRoute(pemesananId))
                },
                onNavigateToPayment = { transaksiId ->
                    navController.navigate(Screen.Payment.createRoute(transaksiId))
                }
            )
        }
        composable(Screen.DetailPemesanan.route) { backStackEntry ->
            val pemesananId = backStackEntry.arguments?.getString("pemesananId") ?: ""
            val detailState by pemesananViewModel.detailState.collectAsStateWithLifecycle()
            LaunchedEffect(pemesananId) {
                if (pemesananId.isNotBlank()) pemesananViewModel.fetchDetailById(pemesananId)
            }

            DetailPemesananScreen(
                detailState = detailState,
                onBackClick = { navController.popBackStack() },
                onMarkFinished = {
                    pemesananViewModel.updateStatus(
                        transaksiId = pemesananId,
                        status = "Selesai",
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                    )
                },
                onCancelClick = {
                    pemesananViewModel.cancelPemesanan(
                        transaksiId = pemesananId,
                        onComplete = {
                            Toast.makeText(context, "Pesanan dibatalkan", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                    )
                },
                onCancelWithReason = { reason ->
                    pemesananViewModel.cancelWithReason(
                        transaksiId = pemesananId,
                        reason = reason,
                        onComplete = {
                            Toast.makeText(context, "Refund diproses & pesanan dibatalkan", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                    )
                },
                onNavigateToPayment = { id -> navController.navigate(Screen.Payment.createRoute(id)) }
            )
        }

        // Feature 3: Ulasan
        composable(Screen.UlasanList.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val ulasanViewModel: UlasanViewModel = viewModel()
            val uiState by ulasanViewModel.ulasanState.collectAsStateWithLifecycle()
            val ratingSummary by ulasanViewModel.ratingSummary.collectAsStateWithLifecycle()
            val selectedFilter by ulasanViewModel.selectedRatingFilter.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) { ulasanViewModel.fetchUlasan(eventId) }

            UlasanListScreen(
                uiState = uiState,
                ratingSummary = ratingSummary,
                selectedFilter = selectedFilter,
                currentUserId = currentUser?.id ?: "",
                actionMessage = ulasanViewModel.actionMessage,
                onFilterClick = ulasanViewModel::onFilterChanged,
                onDeleteClick = { id -> ulasanViewModel.deleteUlasan(id, eventId) },
                onEditClick = { ulasan ->
                    ulasanViewModel.startEdit(ulasan)
                    navController.navigate(Screen.UlasanForm.createRoute(eventId))
                },
                onAddUlasanClick = {
                    if (currentUser == null) {
                        Toast.makeText(context, "Harap masuk (login) untuk menulis ulasan", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    } else {
                        ulasanViewModel.resetForm()
                        navController.navigate(Screen.UlasanForm.createRoute(eventId))
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.UlasanForm.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.UlasanList.route)
            }
            val ulasanViewModel: UlasanViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val eventTitle by ulasanViewModel.eventTitle.collectAsStateWithLifecycle()
            val ratingInput by ulasanViewModel.ratingInput.collectAsStateWithLifecycle()
            val commentInput by ulasanViewModel.commentInput.collectAsStateWithLifecycle()
            val commentError by ulasanViewModel.commentError.collectAsStateWithLifecycle()
            val editingId by ulasanViewModel.editingUlasanId.collectAsStateWithLifecycle()
            val isSubmitting by ulasanViewModel.isSubmitting.collectAsStateWithLifecycle()

            UlasanFormScreen(
                eventTitle = eventTitle,
                isEditMode = editingId != null,
                ratingInput = ratingInput,
                commentInput = commentInput,
                commentError = commentError,
                isSubmitting = isSubmitting,
                onRatingChange = ulasanViewModel::onRatingChange,
                onCommentChange = ulasanViewModel::onCommentChange,
                onSubmit = {
                    val user = currentUser
                    if (user == null) {
                        Toast.makeText(context, "Harap Login Dulu!", Toast.LENGTH_LONG).show()
                    } else {
                        ulasanViewModel.submitUlasan(
                            eventId = eventId,
                            userId = user.id,
                            username = user.fullName?.takeIf { it.isNotBlank() } ?: "User",
                            onSuccess = {
                                Toast.makeText(context, "Ulasan Berhasil!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Feature 4: Wishlist
        composable(Screen.Wishlist.route) {
            val wishlistViewModel: WishlistViewModel = viewModel()
            val wishlistState by wishlistViewModel.wishlistState.collectAsStateWithLifecycle()
            val selectedCategory by wishlistViewModel.selectedCategory.collectAsStateWithLifecycle()
            val totalCount by wishlistViewModel.totalCount.collectAsStateWithLifecycle()

            LaunchedEffect(currentUser?.id) {
                wishlistViewModel.fetchWishlist(currentUser?.id.orEmpty())
            }

            WishlistScreen(
                wishlistState = wishlistState,
                selectedCategory = selectedCategory,
                totalCount = totalCount,
                categories = wishlistViewModel.categories,
                isLoggedIn = currentUser != null,
                onCategorySelect = wishlistViewModel::onCategorySelected,
                onEventClick = { eventId -> navController.navigate(Screen.WishlistDetail.createRoute(eventId)) },
                onFavoriteToggle = { eventId ->
                    wishlistViewModel.removeFromWishlist(
                        eventId = eventId,
                        userIdInput = currentUser?.id.orEmpty(),
                        onSuccess = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            katalogViewModel.syncFavoriteStateLocally(eventId, false)
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.WishlistDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Wishlist.route)
            }
            val wishlistViewModel: WishlistViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val detailState by wishlistViewModel.detailState.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                if (eventId.isNotBlank()) wishlistViewModel.fetchWishlistDetail(eventId)
            }

            WishlistDetailScreen(
                detailState = detailState,
                onBackClick = { navController.popBackStack() },
                onRemoveFromWishlist = { id ->
                    wishlistViewModel.removeFromWishlist(
                        eventId = id,
                        userIdInput = currentUser?.id.orEmpty(),
                        onSuccess = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            katalogViewModel.syncFavoriteStateLocally(id, false)
                            navController.popBackStack()
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onBookClick = { id ->
                    if (currentUser == null) {
                        Toast.makeText(context, "Harap masuk (login) terlebih dahulu", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    } else {
                        navController.navigate(Screen.Checkout.createRoute(id))
                    }
                }
            )
        }

        // Feature 5: Agenda
        composable(Screen.AgendaList.route) { backStackEntry ->
            val agendaViewModel: AgendaViewModel = viewModel(
                viewModelStoreOwner = remember(backStackEntry) { navController.getBackStackEntry(Screen.AgendaList.route) }
            )
            val bookedState by agendaViewModel.bookedState.collectAsStateWithLifecycle()
            val searchQuery by agendaViewModel.searchQuery.collectAsStateWithLifecycle()

            LaunchedEffect(currentUser?.id) { agendaViewModel.loadBookedEvents() }

            BookedEventListScreen(
                state = bookedState,
                query = searchQuery,
                isLoggedIn = currentUser != null,
                onQueryChange = agendaViewModel::onSearchChange,
                onOpenNotes = { eventId -> navController.navigate(Screen.AgendaDetailList.createRoute(eventId)) },
                onExplore = {
                    navController.navigate(Screen.KatalogList.route) {
                        popUpTo(Screen.KatalogList.route) { inclusive = false }
                    }
                },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.AgendaDetailList.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.AgendaList.route) }
            val agendaViewModel: AgendaViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val notesState by agendaViewModel.notesState.collectAsStateWithLifecycle()
            val currentBookedEvent by agendaViewModel.currentBookedEvent.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) { agendaViewModel.loadNotes(eventId) }

            AgendaListScreen(
                state = notesState,
                bookedEvent = currentBookedEvent,
                messageFlow = agendaViewModel.message,
                onBack = { navController.popBackStack() },
                onAddNote = {
                    agendaViewModel.startAdd(eventId)
                    navController.navigate(Screen.AgendaForm.createRoute(eventId))
                },
                onEditNote = { note ->
                    agendaViewModel.startEdit(note, eventId)
                    navController.navigate(Screen.AgendaForm.createRoute(eventId))
                },
                onToggleDone = agendaViewModel::toggleDone,
                onDeleteNote = agendaViewModel::deleteNote
            )
        }
        composable(Screen.AgendaForm.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.AgendaList.route) }
            val agendaViewModel: AgendaViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val titleInput by agendaViewModel.titleInput.collectAsStateWithLifecycle()
            val notesInput by agendaViewModel.notesInput.collectAsStateWithLifecycle()
            val dateInput by agendaViewModel.dateInput.collectAsStateWithLifecycle()
            val isDoneInput by agendaViewModel.isDoneInput.collectAsStateWithLifecycle()
            val titleError by agendaViewModel.titleError.collectAsStateWithLifecycle()
            val dateError by agendaViewModel.dateError.collectAsStateWithLifecycle()
            val isSaving by agendaViewModel.isSaving.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                agendaViewModel.message.collect { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            AgendaFormScreen(
                isEditMode = agendaViewModel.isEditMode,
                titleInput = titleInput,
                notesInput = notesInput,
                dateInput = dateInput,
                isDoneInput = isDoneInput,
                titleError = titleError,
                dateError = dateError,
                isSaving = isSaving,
                onTitleChange = agendaViewModel::onTitleChange,
                onNotesChange = agendaViewModel::onNotesChange,
                onDateChange = agendaViewModel::onDateChange,
                onDoneChange = agendaViewModel::onDoneChange,
                onSave = {
                    agendaViewModel.save(
                        eventIdArg = eventId,
                        onSuccess = { navController.popBackStack() }
                    )
                },
                onDeleteCurrentNote = { onDone -> agendaViewModel.deleteCurrentNote(onDone = onDone) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
