package com.jawnnypoo.openmeh.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jawnnypoo.openmeh.Container
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.resources.Res
import com.jawnnypoo.openmeh.resources.about
import com.jawnnypoo.openmeh.resources.action_account
import com.jawnnypoo.openmeh.resources.action_forum
import com.jawnnypoo.openmeh.resources.action_orders
import com.jawnnypoo.openmeh.resources.action_share
import com.jawnnypoo.openmeh.resources.app_name
import com.jawnnypoo.openmeh.resources.buy_it
import com.jawnnypoo.openmeh.resources.error_with_server
import com.jawnnypoo.openmeh.resources.full_product_specs
import com.jawnnypoo.openmeh.resources.ic_notifications_24dp
import com.jawnnypoo.openmeh.resources.ic_play_196dp
import com.jawnnypoo.openmeh.resources.ic_share_24dp
import com.jawnnypoo.openmeh.resources.notifications
import com.jawnnypoo.openmeh.resources.post_notification
import com.jawnnypoo.openmeh.resources.share_subject
import com.jawnnypoo.openmeh.resources.sold_out
import com.jawnnypoo.openmeh.resources.tap_to_load_again
import com.jawnnypoo.openmeh.resources.url_account
import com.jawnnypoo.openmeh.resources.url_forum
import com.jawnnypoo.openmeh.resources.url_orders
import com.jawnnypoo.openmeh.shared.extension.getCheckoutUrl
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.ui.components.MarkdownBlock
import com.jawnnypoo.openmeh.util.openUrl
import com.jawnnypoo.openmeh.util.shareText
import com.jawnnypoo.openmeh.viewmodel.DealUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealScreen(
    state: DealUiState,
    onRefresh: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenImageViewer: (images: List<String>, index: Int) -> Unit,
    onPostDebugNotification: () -> Unit,
) {
    val response = state.response
    val deal = response?.deal
    val parsedTheme = remember(response) {
        ParsedTheme.fromTheme(response?.deal?.theme)
    }
    val accentColor = parsedTheme?.safeAccentColor() ?: ParsedTheme.WHITE
    val shareSubject = stringResource(Res.string.share_subject)
    val accountUrl = stringResource(Res.string.url_account)
    val ordersUrl = stringResource(Res.string.url_orders)
    val forumUrl = stringResource(Res.string.url_forum)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenNotifications) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_notifications_24dp),
                            contentDescription = stringResource(Res.string.notifications),
                        )
                    }
                    IconButton(
                        onClick = {
                            response?.deal?.let { d ->
                                shareText(d.url, shareSubject)
                            }
                        },
                        enabled = response != null,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_share_24dp),
                            contentDescription = stringResource(Res.string.action_share),
                        )
                    }
                    DealOverflowMenu(
                        onOpenAbout = onOpenAbout,
                        onOpenAccount = { openUrl(accountUrl, accentColor) },
                        onOpenOrders = { openUrl(ordersUrl, accentColor) },
                        onOpenForum = { openUrl(forumUrl, accentColor) },
                        onPostDebugNotification = onPostDebugNotification,
                    )
                },
            )
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                state.isLoading && deal == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                deal == null -> {
                    ErrorState(
                        modifier = Modifier.fillMaxSize(),
                        onRefresh = onRefresh,
                    )
                }

                else -> {
                    DealContent(
                        modifier = Modifier.fillMaxSize(),
                        response = requireNotNull(response),
                        parsedTheme = parsedTheme,
                        onOpenImageViewer = onOpenImageViewer,
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.error_with_server),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRefresh) {
            Text(text = stringResource(Res.string.tap_to_load_again))
        }
    }
}

@Composable
private fun DealContent(
    modifier: Modifier,
    response: MehResponse,
    parsedTheme: ParsedTheme?,
    onOpenImageViewer: (images: List<String>, index: Int) -> Unit,
) {
    val deal = response.deal
    val foregroundColor = parsedTheme?.safeForegroundColor() ?: ParsedTheme.BLACK
    val backgroundColor = parsedTheme?.safeBackgroundColor() ?: ParsedTheme.WHITE
    val accentColor = parsedTheme?.safeAccentColor() ?: ParsedTheme.BLACK

    LazyColumn(
        modifier = modifier.background(Color(backgroundColor)),
    ) {
        item {
            val backgroundImage = parsedTheme?.backgroundImage
            if (backgroundImage != null) {
                AsyncImage(
                    model = backgroundImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .alpha(0.30f),
                )
            }
        }

        item {
            key(deal.id) {
                if (deal.photos.isNotEmpty()) {
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        pageCount = { deal.photos.size },
                    )
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                    ) { page ->
                        AsyncImage(
                            model = deal.photos[page],
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    onOpenImageViewer(deal.photos, pagerState.currentPage)
                                },
                        )
                    }
                    DotIndicator(
                        count = deal.photos.size,
                        selected = pagerState.currentPage,
                        color = Color(foregroundColor),
                    )
                }
            }
        }

        item {
            BuyButton(
                deal = deal,
                accentColor = accentColor,
                backgroundColor = backgroundColor,
                foregroundColor = foregroundColor,
            )
        }

        item {
            Text(
                text = deal.title,
                color = Color(foregroundColor),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }

        item {
            MarkdownBlock(
                markdown = deal.features,
                foregroundColor = Color(foregroundColor),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        deal.topic?.url?.let { topicUrl ->
            item {
                Text(
                    text = stringResource(Res.string.full_product_specs),
                    color = Color(foregroundColor),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .clickable {
                            openUrl(topicUrl, accentColor)
                        },
                )
            }
        }

        item {
            Text(
                text = deal.story.title,
                color = Color(accentColor),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }

        item {
            MarkdownBlock(
                markdown = deal.story.body,
                foregroundColor = Color(foregroundColor),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        response.video?.let { video ->
            item {
                ElevatedCard(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clickable {
                            openUrl(video.url, accentColor)
                        },
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_play_196dp),
                            contentDescription = null,
                            tint = Color(accentColor),
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BuyButton(
    deal: Deal,
    accentColor: Int,
    backgroundColor: Int,
    foregroundColor: Int,
) {
    val soldOut = deal.isSoldOut()
    val label = if (soldOut) {
        stringResource(Res.string.sold_out)
    } else {
        "${deal.getPriceRange()}\n${stringResource(Res.string.buy_it)}"
    }
    Button(
        onClick = {
            openUrl(deal.getCheckoutUrl(), accentColor)
        },
        enabled = !soldOut,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = label,
            color = if (soldOut) Color(backgroundColor) else Color(foregroundColor),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DotIndicator(
    count: Int,
    selected: Int,
    color: Color,
) {
    if (count <= 1) {
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(count) { index ->
            val dotColor = if (index == selected) color else color.copy(alpha = 0.35f)
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
    }
}

@Composable
private fun DealOverflowMenu(
    onOpenAccount: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenForum: () -> Unit,
    onOpenAbout: () -> Unit,
    onPostDebugNotification: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.action_account)) },
            onClick = {
                expanded = false
                onOpenAccount()
            },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.action_orders)) },
            onClick = {
                expanded = false
                onOpenOrders()
            },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.action_forum)) },
            onClick = {
                expanded = false
                onOpenForum()
            },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.about)) },
            onClick = {
                expanded = false
                onOpenAbout()
            },
        )
        if (Container.debug) {
            DropdownMenuItem(
                text = { Text(text = stringResource(Res.string.post_notification)) },
                onClick = {
                    expanded = false
                    onPostDebugNotification()
                },
            )
        }
    }
}
