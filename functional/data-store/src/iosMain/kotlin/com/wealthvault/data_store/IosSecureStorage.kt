@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.cinterop.BetaInteropApi::class,
    kotlinx.cinterop.UnsafeNumber::class,
)

package com.wealthvault.data_store

import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryGetValue
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecDuplicateItem
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

private const val KEYCHAIN_SERVICE = "com.wealthvault.session"

class IosSecureStorage : SecureStorage {
    override suspend fun read(key: String): String? = withKeyQuery(key, includeData = true) { query ->
        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, result.ptr)
            if (status != errSecSuccess) return@memScoped null
            val data = CFBridgingRelease(result.value) as? NSData ?: return@memScoped null
            NSString.create(data, NSUTF8StringEncoding)?.toString()
        }
    }

    override suspend fun write(key: String, value: String) {
        // A failed encoding must fail the migration/write. Silently returning here
        // would make TokenStore believe the value was persisted and could allow it
        // to remove the legacy token before Keychain contains a readable copy.
        val data = checkNotNull(NSString.create(value).dataUsingEncoding(NSUTF8StringEncoding)) {
            "Unable to encode secure session value"
        }
        withRetained(data) { retainedData ->
            withKeyQuery(key, includeData = false) { query ->
                val attributes = cfDictionaryOf(kSecValueData to retainedData)
                val updateStatus = SecItemUpdate(query, attributes)
                CFBridgingRelease(attributes)
                if (updateStatus == errSecItemNotFound) {
                    val insert = cfDictionaryWithData(query, retainedData)
                    val insertStatus = SecItemAdd(insert, null)
                    CFBridgingRelease(insert)
                    check(insertStatus == errSecSuccess || insertStatus == errSecDuplicateItem) {
                        "Unable to write secure session value"
                    }
                } else {
                    check(updateStatus == errSecSuccess) { "Unable to update secure session value" }
                }
            }
        }
    }

    override suspend fun remove(key: String) {
        withKeyQuery(key, includeData = false) { query ->
            val status = SecItemDelete(query)
            check(status == errSecSuccess || status == errSecItemNotFound)
        }
    }

    override suspend fun clear() {
        withRetained(KEYCHAIN_SERVICE) { service ->
            val query = cfDictionaryOf(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to service,
            )
            val status = SecItemDelete(query)
            CFBridgingRelease(query)
            check(status == errSecSuccess || status == errSecItemNotFound)
        }
    }

    private inline fun <T> withKeyQuery(
        key: String,
        includeData: Boolean,
        block: (CFDictionaryRef?) -> T,
    ): T = withRetained(key) { account ->
        withRetained(KEYCHAIN_SERVICE) { service ->
            val query = cfDictionaryOf(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to service,
                kSecAttrAccount to account,
                kSecMatchLimit to kSecMatchLimitOne,
                kSecReturnData to if (includeData) kCFBooleanTrue else kCFBooleanFalse,
            )
            try {
                block(query)
            } finally {
                CFBridgingRelease(query)
            }
        }
    }

    private inline fun <T> withRetained(value: Any?, block: (CFTypeRef?) -> T): T {
        val retained = CFBridgingRetain(value)
        return try {
            block(retained)
        } finally {
            CFBridgingRelease(retained)
        }
    }
}

private fun cfDictionaryWithData(query: CFDictionaryRef?, data: CFTypeRef?): CFDictionaryRef? =
    memScoped {
        cfDictionaryOf(
            kSecClass to CFDictionaryGetValue(query, kSecClass),
            kSecAttrService to CFDictionaryGetValue(query, kSecAttrService),
            kSecAttrAccount to CFDictionaryGetValue(query, kSecAttrAccount),
            kSecValueData to data,
        )
    }

private fun cfDictionaryOf(vararg items: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef? =
    memScoped {
        val keys = allocArrayOf(*items.map { it.first }.toTypedArray())
        val values = allocArrayOf(*items.map { it.second }.toTypedArray())
        CFDictionaryCreate(
            kCFAllocatorDefault,
            keys.reinterpret(),
            values.reinterpret(),
            items.size.convert(),
            null,
            null,
        )
    }
