package com.example.flightsearch.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flightsearch.R
import com.example.flightsearch.ui.navigation.FlightSearchNavGraph
import com.example.flightsearch.ui.navigation.HomeRoute

@Composable
fun FlightSearchApp(navController: NavHostController = rememberNavController()) {
    Scaffold(
        topBar = { FlightSearchTopAppBar(navController) }
    ) { innerPadding ->
        FlightSearchNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchTopAppBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val title = when {
        navBackStackEntry?.destination?.hasRoute<HomeRoute>() == true -> stringResource(R.string.app_name)
        else -> stringResource(R.string.app_name)
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { Text(title) },
        modifier = modifier
    )
}