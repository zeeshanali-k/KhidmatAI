package com.corestack.khidmatai.admin.ui.providers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.corestack.khidmatai.admin.ui.components.*
import com.corestack.khidmatai.core.domain.model.AdminProvider
import com.corestack.khidmatai.core.domain.model.AdminState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProviderFormScreen(providerId: String?, navController: NavController) {
    val vm: ProvidersViewModel = koinViewModel()
    val formState by vm.formState.collectAsStateWithLifecycle()

    LaunchedEffect(providerId) {
        if (providerId != null) vm.loadForEdit(providerId)
    }

    val isEdit = providerId != null
    val existing = (formState as? com.corestack.khidmatai.core.domain.model.AdminState.Success)?.data

    var name by remember(existing) { mutableStateOf(existing?.name ?: "") }
    var serviceType by remember(existing) { mutableStateOf(existing?.serviceType ?: "") }
    var phone by remember(existing) { mutableStateOf(existing?.phone ?: "") }
    var rating by remember(existing) { mutableStateOf(existing?.rating?.toString() ?: "4.0") }
    var pricePerHour by remember(existing) { mutableStateOf(existing?.pricePerHour?.toString() ?: "1000") }
    var experienceYears by remember(existing) { mutableStateOf(existing?.experienceYears?.toString() ?: "1") }
    var address by remember(existing) { mutableStateOf(existing?.locationAddress ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScreenHeader(
                title = if (isEdit) "Edit Provider" else "New Provider",
                action = { PrimaryButton("Back", onClick = { navController.popBackStack() }) }
            )
        }

        if (isEdit && formState is com.corestack.khidmatai.core.domain.model.AdminState.Loading) {
            item { LoadingBox(Modifier.height(200.dp)) }
            return@LazyColumn
        }

        item {
            AdminCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    AdminTextField(value = name, onValueChange = { name = it }, label = "Name", modifier = Modifier.fillMaxWidth())
                    AdminTextField(value = serviceType, onValueChange = { serviceType = it }, label = "Service Type (e.g. plumber)", modifier = Modifier.fillMaxWidth())
                    AdminTextField(value = phone, onValueChange = { phone = it }, label = "Phone", modifier = Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminTextField(value = rating, onValueChange = { rating = it }, label = "Rating (0-5)", modifier = Modifier.weight(1f))
                        AdminTextField(value = pricePerHour, onValueChange = { pricePerHour = it }, label = "Price/hr (PKR)", modifier = Modifier.weight(1f))
                        AdminTextField(value = experienceYears, onValueChange = { experienceYears = it }, label = "Experience (yrs)", modifier = Modifier.weight(1f))
                    }
                    AdminTextField(value = address, onValueChange = { address = it }, label = "Address", modifier = Modifier.fillMaxWidth())
                }
            }
        }

        item {
            val isValid = name.isNotBlank() && serviceType.isNotBlank() && phone.isNotBlank()
            PrimaryButton(
                text = if (isEdit) "Save Changes" else "Create Provider",
                enabled = isValid,
                onClick = {
                    val provider =
                        _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminProvider(
                            id = providerId ?: "",
                            name = name,
                            serviceType = serviceType.lowercase().replace(" ", "_"),
                            rating = rating.toFloatOrNull() ?: 4f,
                            phone = phone,
                            pricePerHour = pricePerHour.toDoubleOrNull() ?: 1000.0,
                            experienceYears = experienceYears.toIntOrNull() ?: 1,
                            availability = existing?.availability ?: true,
                            locationAddress = address
                        )
                    if (isEdit && providerId != null) {
                        vm.update(providerId, provider) { navController.popBackStack() }
                    } else {
                        vm.create(provider) { navController.popBackStack() }
                    }
                }
            )
        }

        if (formState is com.corestack.khidmatai.core.domain.model.AdminState.Error) {
            item {
                Text(
                    text = (formState as com.corestack.khidmatai.core.domain.model.AdminState.Error).message,
                    color = StatusCancelled
                )
            }
        }
    }
}
