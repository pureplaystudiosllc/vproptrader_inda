package com.futureharvest.vproptrader
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//
///**
// * Trading Account Purchase Screen
// * Implements Material Design 3 principles with latest Compose best practices
// */
//@Composable
//fun TradingAccountPurchaseScreen(
//    viewModel: TradingAccountViewModel,
//    onProductSelected: (BillingManager.TradingAccountProduct) -> Unit,
//    onBackPressed: () -> Unit
//) {
//    val context = LocalContext.current
//    val products by viewModel.getProducts().collectAsStateWithLifecycle(initialValue = emptyList())
//    val isLoading by viewModel.getIsLoading().collectAsStateWithLifecycle(initialValue = false)
//    val errorMessage by viewModel.getErrorMessage().collectAsStateWithLifecycle(initialValue = null)
//
//    MaterialTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//                // Header with back button
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    IconButton(onClick = onBackPressed) {
//                        Icon(
//                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//
//                    Text(
//                        text = "Select Trading Account",
//                        style = MaterialTheme.typography.headlineMedium,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//
//                // Error message
//                errorMessage?.let { error ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 16.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = MaterialTheme.colorScheme.errorContainer
//                        )
//                    ) {
//                        Text(
//                            text = error,
//                            color = MaterialTheme.colorScheme.onErrorContainer,
//                            modifier = Modifier.padding(16.dp)
//                        )
//                    }
//                }
//
//                // Loading indicator
//                if (isLoading) {
//                    Box(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                } else {
//                    // Product list
//                    if (products.isNotEmpty()) {
//                        TradingAccountProductList(
//                            products = products,
//                            onProductSelected = onProductSelected
//                        )
//                    } else {
//                        // Empty state
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Icon(
//                                    imageVector = androidx.compose.material.icons.Icons.Default.AccountBalance,
//                                    contentDescription = null,
//                                    modifier = Modifier.size(64.dp),
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                                Spacer(modifier = Modifier.height(16.dp))
//                                Text(
//                                    text = "No trading accounts available",
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TradingAccountProductList(
//    products: List<BillingManager.TradingAccountProduct>,
//    onProductSelected: (BillingManager.TradingAccountProduct) -> Unit
//) {
//    LazyColumn(
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(products) { product ->
//            TradingAccountProductCard(
//                product = product,
//                onProductSelected = onProductSelected
//            )
//        }
//    }
//}
//
//@Composable
//fun TradingAccountProductCard(
//    product: BillingManager.TradingAccountProduct,
//    onProductSelected: (BillingManager.TradingAccountProduct) -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        )
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            // Product name
//            Text(
//                text = product.getName(),
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//            // Description
//            Text(
//                text = product.getDescription(),
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(bottom = 12.dp)
//            )
//
//            // Account balance
//            Row(
//                modifier = Modifier.padding(bottom = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = androidx.compose.material.icons.Icons.Default.AccountBalance,
//                    contentDescription = null,
//                    modifier = Modifier.size(20.dp),
//                    tint = MaterialTheme.colorScheme.primary
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Account Balance: $${String.format("%,.0f", product.getAccountBalance())}",
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//
//            // Price and purchase button
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(
//                        text = "$${String.format("%.2f", product.getPrice())}",
//                        style = MaterialTheme.typography.headlineSmall,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                    Text(
//                        text = product.getPriceCurrency(),
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//
//                Button(
//                    onClick = { onProductSelected(product) },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary
//                    ),
//                    modifier = Modifier.height(48.dp)
//                ) {
//                    Text(
//                        text = "Purchase",
//                        style = MaterialTheme.typography.labelLarge
//                    )
//                }
//            }
//        }
//    }
//}
