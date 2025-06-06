package com.example.huckathon.presentation.screens.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.huckathon.R
import com.example.huckathon.domain.models.Friend
import com.example.huckathon.presentation.navigation.Screen
import com.example.huckathon.presentation.screens.profile.components.ProfileHeader
import com.example.huckathon.presentation.screens.profile.viewmodel.ProfileViewModel


@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            NavigationBar(containerColor = BackgroundColor) {
                val items = listOf(
                    Screen.MapScreen to R.drawable.harita,
                    Screen.Chatbot   to R.drawable.chatbot,
                    Screen.Profile   to R.drawable.profile,
                    Screen.Settings  to R.drawable.settings
                )
                val currentRoute = navController.currentBackStackEntryAsState().value
                    ?.destination?.route

                items.forEach { (screen, iconRes) ->
                    NavigationBarItem(
                        icon = { androidx.compose.material3.Icon(
                            painter = painterResource(iconRes),
                            contentDescription = screen.route
                        ) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            when (screen) {
                                Screen.MapScreen   -> navController.navigate(screen.route)
                                Screen.Profile     -> { /* already here */ }
                                Screen.Chatbot     -> { navController.navigate(screen.route)}
                                Screen.Settings    -> { /* TODO */ }
                                else         -> navController.navigate(screen.route) {
                                    popUpTo(Screen.Profile.route) { inclusive = true }
                                }
                            }
                        },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor   = PrimaryTextColor,
                            unselectedIconColor = SecondaryTextColor,
                            indicatorColor      = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            ProfileHeader(
                userName = profile.userName,
                userLevel = profile.userLevel
            )

            Spacer(Modifier.height(24.dp))

            EcoScoreCard(score = profile.ecoScore)

            Spacer(Modifier.height(28.dp))

            EcoWarriorCard(distanceKm = profile.warriorDistanceKm)

            Spacer(Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(21.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricCard(iconRes = R.drawable.steps_icon, value = profile.steps.toString(), label = "Steps")
                MetricCard(iconRes = R.drawable.km, value = "${profile.distanceKm}", label = "KM")
                MetricCard(iconRes = R.drawable.kg, value = "${profile.weightKg}", label = "KG")
            }

            Spacer(Modifier.height(28.dp))

            FriendsBox(friends = profile.friends, onViewAll = { /* TODO */ })
        }
    }
}
@Composable
fun EcoScoreCard(score: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(CardBackgroundColor)
                .border(17.dp, Color(0xFF4CAF50), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = score.toString(),
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.W500,
                    fontSize = 45.sp
                )
                Spacer(modifier = Modifier.height(6 .dp))

                Text(
                    text = "Eco-Score",
                    color = SecondaryTextColor,
                    fontWeight = FontWeight.W400,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
fun EcoWarriorCard(
    distanceKm: Double,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackgroundColor)
            .padding(20.dp).padding(start = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.eco_warrior),
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Eco Warrior",
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Active, ${distanceKm} km",
                    color = SecondaryTextColor
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    iconRes: Int,
    value: String,
    label: String
) {
    Box(
        modifier = Modifier
            .height(120.dp).width(112.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackgroundColor)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = SecondaryTextColor
            )
        }
    }
}

@Composable
fun FriendsBox(
    friends: List<Friend>,
    onViewAll: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackgroundColor)
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Friends",
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All",
                    color = Color(0xFF2196F3),
                    modifier = Modifier.clickable { onViewAll() }
                )
            }
            Spacer(Modifier.height(12.dp))
            Row {
                friends.take(3).forEach { friend ->
                    Image(
                        painter = painterResource(friend.avatarRes),
                        contentDescription = friend.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                if (friends.size > 3) {
                    Text(
                        text = "+${friends.size - 3}",
                        color = SecondaryTextColor,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}
