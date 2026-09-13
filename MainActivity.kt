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
    }
}

@Composable
fun App() {
    var splash by remember { mutableStateOf(true) }
    var tab by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Service?>(null) }
    var dark by remember { mutableStateOf(false) }
    val requests = remember { mutableStateListOf<Request>() }

    LaunchedEffect(Unit) { delay(1400); splash = false }

    MaterialTheme(
        colorScheme = if (dark) darkColorScheme(primary = Gold, secondary = Gold)
        else lightColorScheme(primary = Green, secondary = Gold)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            when {
                splash -> Splash()
                selected != null -> Form(
                    service = selected!!,
                    back = { selected = null },
                    submit = { request ->
                        requests.add(0, request)
                        selected = null
                        tab = 2
                    }
                )
                else -> Main(
                    tab = tab,
                    setTab = { tab = it },
                    requests = requests,
                    dark = dark,
                    setDark = { dark = it },
                    open = { selected = it }
                )
            }
        }
    }
}

@Composable
fun Splash() {
    Surface(Modifier.fillMaxSize(), color = Green) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("✦", color = Gold, fontSize = 76.sp)
            Spacer(Modifier.height(18.dp))
            Text("منصة الخدمات الإلكترونية", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("نسخة تجريبية غير رسمية", color = Gold, fontSize = 13.sp)
        }
    }
}

@Composable
fun Main(
    tab: Int, setTab: (Int) -> Unit, requests: List<Request>,
    dark: Boolean, setDark: (Boolean) -> Unit, open: (Service) -> Unit
) {
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(tab == 0, { setTab(0) }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("الرئيسية") })
            NavigationBarItem(tab == 1, { setTab(1) }, icon = { Icon(Icons.Default.Apps, null) }, label = { Text("الخدمات") })
            NavigationBarItem(tab == 2, { setTab(2) }, icon = { Icon(Icons.Default.List, null) }, label = { Text("الطلبات") })
            NavigationBarItem(tab == 3, { setTab(3) }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("الإعدادات") })
        }
    }) { p ->
        when (tab) {
            0 -> Home(Modifier.padding(p), requests, open)
            1 -> Services(Modifier.padding(p), open)
            2 -> Requests(Modifier.padding(p), requests)
            else -> Settings(Modifier.padding(p), dark, setDark)
        }
    }
}

@Composable
fun Home(modifier: Modifier, requests: List<Request>, open: (Service) -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(20.dp)) {
        item {
            Text("مرحبًا بك", color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
            Text("منصة الخدمات الإلكترونية", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(18.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Column(Modifier.padding(20.dp)) {
                    Text("أنجز خدماتك بسهولة", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("استعرض الخدمات وأنشئ طلبًا تجريبيًا وتابع حالته.", color = Color.White.copy(alpha = .85f))
                    Spacer(Modifier.height(10.dp))
                    Text("نسخة تجريبية غير رسمية", color = Gold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("الخدمات الرئيسية", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
        }
        items(services.take(4)) { ServiceCard(it, open) }
        item { Spacer(Modifier.height(12.dp)); Text("عدد الطلبات: ${requests.size}") }
    }
}

@Composable
fun Services(modifier: Modifier, open: (Service) -> Unit) {
    var query by remember { mutableStateOf("") }
    val result = services.filter { it.title.contains(query) || it.description.contains(query) }
    Column(modifier.fillMaxSize().padding(20.dp)) {
        Text("جميع الخدمات", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text("ابحث عن خدمة") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        LazyColumn { items(result) { ServiceCard(it, open); Spacer(Modifier.height(10.dp)) } }
    }
}

@Composable
fun ServiceCard(service: Service, open: (Service) -> Unit) {
    Card(onClick = { open(service) }, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(service.title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(service.description, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronLeft, null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form(service: Service, back: () -> Unit, submit: (Request) -> Unit) {
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(title = { Text(service.title) }, navigationIcon = {
            IconButton(onClick = back) { Icon(Icons.Default.ArrowBack, "رجوع") }
        })
    }) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p).padding(20.dp)) {
            item {
                Text("نموذج طلب تجريبي", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("لا تدخل بيانات حقيقية حساسة في هذه النسخة.", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(name, { name = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("الاسم التجريبي") }, singleLine = true)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(id, { id = it; error = "" }, Modifier.fillMaxWidth(), label = { Text("رقم تعريفي تجريبي") }, singleLine = true)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(details, { details = it; error = "" }, Modifier.fillMaxWidth().height(130.dp), label = { Text("تفاصيل الطلب") })
                Spacer(Modifier.height(12.dp))
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    when {
                        name.trim().length < 3 -> error = "أدخل اسمًا تجريبيًا صحيحًا."
                        id.trim().length < 4 -> error = "أدخل رقمًا تجريبيًا صحيحًا."
                        details.trim().length < 5 -> error = "اكتب تفاصيل الطلب."
                        else -> {
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
