package com.managevent.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.ui.KatalogDetailScreen
import com.managevent.app.features.katalog.ui.KatalogFormScreen
import com.managevent.app.features.katalog.ui.KatalogListScreen
import com.managevent.app.features.katalog.viewmodel.KatalogViewModel
import com.managevent.app.features.pemesanan.ui.CheckoutScreen
import com.managevent.app.features.pemesanan.ui.DetailPemesananScreen
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
    startDestination: String = Screen.KatalogList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Routes
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onSkipClick = { navController.navigate(Screen.KatalogList.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onFinishOnboarding = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                    navController.navigate(Screen.KatalogList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = viewModel()
            RegisterScreen(
                onRegisterClick = { email, password, fullName, role ->
                    authViewModel.register(email, password, fullName, role)
                    navController.navigate(Screen.KatalogList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogoutClick = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Feature 1: Katalog (Muhammad Abdul Fatah)
        composable(Screen.KatalogList.route) {
            val katalogViewModel: KatalogViewModel = viewModel()
            val eventsState by katalogViewModel.eventsState.collectAsStateWithLifecycle()
            val searchQuery by katalogViewModel.searchQuery.collectAsStateWithLifecycle()
            val selectedCategory by katalogViewModel.selectedCategory.collectAsStateWithLifecycle()

            KatalogListScreen(
                eventsState = eventsState,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                onEventClick = { eventId -> navController.navigate(Screen.KatalogDetail.createRoute(eventId)) },
                onFavoriteToggle = katalogViewModel::toggleFavorite,
                onSearchQueryChange = katalogViewModel::onSearchQueryChange,
                onCategorySelected = katalogViewModel::onCategorySelected,
                onAddEventClick = { navController.navigate(Screen.KatalogForm.route) }
            )
        }
        composable(Screen.KatalogDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val katalogViewModel: KatalogViewModel = viewModel()
            val detailState by katalogViewModel.eventDetailState.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                if (eventId.isNotBlank()) {
                    katalogViewModel.fetchEventById(eventId)
                }
            }

            KatalogDetailScreen(
                detailState = detailState,
                onBackClick = { navController.popBackStack() },
                onFavoriteToggle = {
                    katalogViewModel.toggleFavorite(eventId)
                    katalogViewModel.fetchEventById(eventId)
                },
                onBookClick = { id -> navController.navigate(Screen.Checkout.createRoute(id)) },
                onReviewClick = { id -> navController.navigate(Screen.UlasanList.createRoute(id)) }
            )
        }
        composable(Screen.KatalogForm.route) {
            val katalogViewModel: KatalogViewModel = viewModel()
            KatalogFormScreen(
                onBackClick = { navController.popBackStack() },
                onSuccessSubmit = {
                    katalogViewModel.fetchEvents()
                    navController.popBackStack()
                }
            )
        }

        // Feature 2: Pemesanan (Muhammad Darren Achilles Setiawan)
        composable(Screen.Checkout.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            val pemesananViewModel: PemesananViewModel = viewModel()
            val katalogViewModel: KatalogViewModel = viewModel()

            val checkoutState by pemesananViewModel.checkoutState.collectAsStateWithLifecycle()
            val eventState by katalogViewModel.eventDetailState.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                katalogViewModel.fetchEventById(eventId)
            }

            LaunchedEffect(checkoutState) {
                if (checkoutState is UiState.Success) {
                    pemesananViewModel.consumeCheckoutSuccess()
                    navController.navigate(Screen.RiwayatPemesanan.route) {
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
        composable(Screen.RiwayatPemesanan.route) {
            val pemesananViewModel: PemesananViewModel = viewModel()
            val riwayatState by pemesananViewModel.riwayatState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                pemesananViewModel.fetchRiwayat()
            }

            RiwayatPemesananScreen(
                riwayatState = riwayatState,
                onBackClick = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.KatalogList.route) {
                            popUpTo(Screen.KatalogList.route) { inclusive = true }
                        }
                    }
                },
                onTransactionClick = { pemesananId ->
                    navController.navigate(Screen.DetailPemesanan.createRoute(pemesananId))
                }
            )
        }
        composable(Screen.DetailPemesanan.route) { backStackEntry ->
            val pemesananId = backStackEntry.arguments?.getString("pemesananId") ?: ""
            val pemesananViewModel: PemesananViewModel = viewModel()
            val detailState by pemesananViewModel.detailState.collectAsStateWithLifecycle()

            LaunchedEffect(pemesananId) {
                if (pemesananId.isNotBlank()) {
                    pemesananViewModel.fetchDetailById(pemesananId)
                }
            }

            DetailPemesananScreen(
                detailState = detailState,
                onBackClick = { navController.popBackStack() },
                onMarkFinished = { pemesananViewModel.updateStatus(pemesananId, "Selesai") },
                onCancelClick = { pemesananViewModel.cancelPemesanan(pemesananId) }
            )
        }

        // Feature 3: Ulasan (Rayhan Wira Pamungkas)
        composable(Screen.UlasanList.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: "dummy-event-id"
            val ulasanViewModel: UlasanViewModel = viewModel()
            val uiState by ulasanViewModel.ulasanState.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                ulasanViewModel.fetchUlasan(eventId)
            }

            UlasanListScreen(
                uiState = uiState,
                selectedFilter = ulasanViewModel.selectedRatingFilter,
                onFilterClick = ulasanViewModel::onFilterChanged,
                onDeleteClick = { id -> ulasanViewModel.deleteUlasan(id, eventId) },
                onAddUlasanClick = {
                    navController.navigate(Screen.UlasanForm.createRoute(eventId))
                }
            )
        }
        composable(Screen.UlasanForm.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val ulasanViewModel: UlasanViewModel = viewModel()

            UlasanFormScreen(
                eventId = eventId,
                ratingInput = ulasanViewModel.ratingInput,
                commentInput = ulasanViewModel.commentInput,
                onRatingChange = ulasanViewModel::onRatingChange,
                onCommentChange = ulasanViewModel::onCommentChange,
                onSubmit = {
                    ulasanViewModel.submitUlasan(
                        eventId = eventId,
                        userId = "dummy-user-id",
                        username = "User Uji",
                        onSuccess = { navController.popBackStack() }
                    )
                }
            )
        }

        // Feature 4: Wishlist (Muhammad Syauqi Imaduddin)
        composable(Screen.Wishlist.route) {
            val wishlistViewModel: WishlistViewModel = viewModel()
            val wishlistState by wishlistViewModel.wishlistState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                wishlistViewModel.fetchWishlist()
            }

            WishlistScreen(
                wishlistState = wishlistState,
                selectedCategory = wishlistViewModel.selectedCategory,
                totalCount = wishlistViewModel.totalCount,
                categories = wishlistViewModel.categories,
                onCategorySelect = wishlistViewModel::onCategorySelected,
                onEventClick = { eventId ->
                    navController.navigate(Screen.KatalogDetail.createRoute(eventId))
                },
                onFavoriteToggle = wishlistViewModel::removeFromWishlist
            )
        }
        composable(Screen.WishlistDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            WishlistDetailScreen(eventId = eventId)
        }

        // Feature 5: Agenda (Farrel Radya Putra)
        composable(Screen.AgendaList.route) { backStackEntry ->
            val agendaViewModel: AgendaViewModel = viewModel(
                viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.AgendaList.route)
                }
            )
            val bookedState by agendaViewModel.bookedState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                agendaViewModel.loadBookedEvents()
            }

            BookedEventListScreen(
                state = bookedState,
                query = agendaViewModel.searchQuery,
                onQueryChange = agendaViewModel::onSearchChange,
                onOpenNotes = { eventId ->
                    navController.navigate(Screen.AgendaDetailList.createRoute(eventId))
                }
            )
        }
        composable(Screen.AgendaDetailList.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AgendaList.route)
            }
            val agendaViewModel: AgendaViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val notesState by agendaViewModel.notesState.collectAsStateWithLifecycle()

            LaunchedEffect(eventId) {
                agendaViewModel.loadNotes(eventId)
            }

            AgendaListScreen(
                state = notesState,
                bookedEvent = agendaViewModel.currentBookedEvent,
                onBack = { navController.popBackStack() },
                onAddNote = {
                    agendaViewModel.startAdd()
                    navController.navigate(Screen.AgendaForm.route)
                },
                onEditNote = { note ->
                    agendaViewModel.startEdit(note)
                    navController.navigate(Screen.AgendaForm.route)
                },
                onToggleDone = agendaViewModel::toggleDone,
                onDeleteNote = agendaViewModel::deleteNote
            )
        }
        composable(Screen.AgendaForm.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AgendaList.route)
            }
            val agendaViewModel: AgendaViewModel = viewModel(viewModelStoreOwner = parentEntry)

            AgendaFormScreen(
                isEditMode = agendaViewModel.isEditMode,
                titleInput = agendaViewModel.titleInput,
                notesInput = agendaViewModel.notesInput,
                dateInput = agendaViewModel.dateInput,
                isDoneInput = agendaViewModel.isDoneInput,
                titleError = agendaViewModel.titleError,
                dateError = agendaViewModel.dateError,
                isSaving = agendaViewModel.isSaving,
                onTitleChange = agendaViewModel::onTitleChange,
                onNotesChange = agendaViewModel::onNotesChange,
                onDateChange = agendaViewModel::onDateChange,
                onDoneChange = agendaViewModel::onDoneChange,
                onSave = {
                    agendaViewModel.save(onSuccess = { navController.popBackStack() })
                },
                onDeleteCurrentNote = { onDone ->
                    agendaViewModel.deleteCurrentNote(onDone = onDone)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
