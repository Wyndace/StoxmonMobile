package com.stoxmon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stoxmon.ui.screens.news.NewsScreen
import com.stoxmon.ui.screens.portfolio.PortfolioScreen
import com.stoxmon.ui.screens.ticker.TickerDetailScreen
import com.stoxmon.ui.screens.ticker.TickerListScreen

sealed class Screen(val route: String) {
    object Portfolio : Screen("portfolio")
    object Ticker : Screen("ticker/{portfolioId}") {
        fun createRoute(portfolioId: String) = "ticker/$portfolioId"
    }
    object News : Screen("news")
    object TickerDetail : Screen("ticker_detail/{tickerSymbol}") {
        fun createRoute(tickerSymbol: String) = "ticker_detail/$tickerSymbol"
    }
}

@Composable
fun StoxmonNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Portfolio.route
    ) {
        composable(Screen.Portfolio.route) {
            PortfolioScreen(
                onPortfolioClick = { portfolioId ->
                    navController.navigate(Screen.Ticker.createRoute(portfolioId))
                }
            )
        }
        
        composable(
            route = Screen.Ticker.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }
            )
        ) {
            TickerListScreen(
                onTickerClick = { tickerSymbol ->
                    navController.navigate(Screen.TickerDetail.createRoute(tickerSymbol))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.News.route) {
            NewsScreen(
                onNewsClick = { newsId ->
                }
            )
        }
        
        composable(
            route = Screen.TickerDetail.route,
            arguments = listOf(
                navArgument("tickerSymbol") { type = NavType.StringType }
            )
        ) {
            TickerDetailScreen(
                onBackClick = {
                    navController.popBackStack();
                }
            )
        }
    }
}
