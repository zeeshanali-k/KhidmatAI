package com.corestack.khidmatai.data.location

import org.koin.core.annotation.Single

@Single
class LocationPreferences {
    var detectedLocation: String = "--"
}
