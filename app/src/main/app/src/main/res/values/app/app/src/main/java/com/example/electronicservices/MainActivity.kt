package com.example.electronicservices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

private val Green = Color(0xFF123C32)
private val Gold = Color(0xFFD4AF37)

data class Service(val title: String, val description: String)
data class Request(val ref: String, val service: String, val name: String, val date: String)

private val services = listOf(
    Service("منع السفر", "استعلام تجريبي عن منع السفر"),
    Service("حالة موقوف", "استعلام تجريبي عن حالة موقوف"),
    Service("القيد المدني", "طلب قيد مدني تجريبي"),
    Service("جواز السفر", "طلب أو متابعة جواز سفر"),
    Service("تقديم شكوى", "تقديم شكوى تجريبية"),
    Service("المركبات", "معلومات مركبة تجريبية"),
    Service("السجل العدلي", "طلب سجل عدلي تجريبي"),
    Service("البطاقة الشخصية", "طلب متعلق بالبطاقة الشخصية"),
    Service("السجل التجاري", "طلب سجل تجاري تجريبي"),
    Service("الرقم الضريبي", "طلب رقم ضريبي تجريبي")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }                        else -> {
                            val now = Date()
                            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(now)
                            val day = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(now)
                            submit(Request("ES-$day-${Random.nextInt(1000, 9999)}", service.title, name, date))
                        }
                    }
                }, Modifier.fillMaxWidth()) { Text("إرسال الطلب التجريبي") }
            }
        }
    }
}

@Composable
fun Requests(modifier: Modifier, requests: List<Request>) {
    Column(modifier.fillMaxSize().padding(20.dp)) {
        Text("طلباتي", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        if (requests.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("لا توجد طلبات حتى الآن") }
        } else {
            LazyColumn { items(requests) { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(r.service, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("رقم الطلب: ${r.ref}")
                        Text("مقدم الطلب: ${r.name}")
                        Text("التاريخ: ${r.date}")
                        Text("الحالة: جديد")
                        Text("طلب تجريبي غير مرتبط بجهة حكومية.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
            } }
        }
    }
}

@Composable
fun Settings(modifier: Modifier, dark: Boolean, setDark: (Boolean) -> Unit) {
    Column(modifier.fillMaxSize().padding(20.dp)) {
        Text("الإعدادات", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("الوضع الداكن", fontWeight = FontWeight.Bold)
                    Text("تغيير مظهر التطبيق")
                }
                Switch(dark, setDark)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("الإصدار 1.0.0 التجريبي")
        Text("هذا التطبيق نموذج غير رسمي ولا يتصل بجهة حكومية.", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
    }
}
}
