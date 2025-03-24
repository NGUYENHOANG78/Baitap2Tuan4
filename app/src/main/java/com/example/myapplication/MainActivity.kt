package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
            MyApplicationTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "tasks") {
        composable("tasks") { TasksScreen(navController) }
        composable("task_detail/{title}/{description}/{category}/{status}/{priority}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")?.replace("%20", " ") ?: "No Title"
            val description = backStackEntry.arguments?.getString("description")?.replace("%20", " ") ?: "No Description"
            val category = backStackEntry.arguments?.getString("category") ?: "Unknown"
            val status = backStackEntry.arguments?.getString("status") ?: "Unknown"
            val priority = backStackEntry.arguments?.getString("priority") ?: "Unknown"
            TaskDetailScreen(navController, title, description, category, status, priority)
        }
    }

}

@Composable
fun TasksScreen(navController: NavHostController) {
    val tasks = listOf(
        TaskData("Complete Android Project", "Finish the UI, integrate API, and write documentation", "Work", "In Progress", "High"),
        TaskData("Fix API Errors", "Resolve API issues and update documentation", "Development", "Pending", "Medium"),
        TaskData("Prepare Presentation", "Create slides and practice for the meeting", "Work", "Completed", "Low")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            items(tasks) { taskData ->
                TaskItem(taskData) {
                    val encodedTitle = taskData.title.replace(" ", "%20")
                    val encodedDesc = taskData.description.replace(" ", "%20")
                    navController.navigate("task_detail/$encodedTitle/$encodedDesc/${taskData.category}/${taskData.status}/${taskData.priority}")
                }
            }
        }
    }
}

// Dữ liệu task
data class TaskData(
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val priority: String
)

// Item hiển thị danh sách task
@Composable
fun TaskItem(task: TaskData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = false, onCheckedChange = {})
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = task.description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}




@Composable
fun TaskItem(task: String, description: String, onClick: () -> Unit) {
    val currentDate = remember {
        SimpleDateFormat("HH:mm yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    var checked by rememberSaveable { mutableStateOf(false) } // Giữ trạng thái khi xoay màn hình

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp)
            .clickable { onClick() }, // Bấm vào toàn bộ item để mở chi tiết
        colors = cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hộp kiểm
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Nội dung nhiệm vụ
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = task, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Ngày & Giờ
            Text(text = currentDate, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Thay bằng file logo của bạn
            contentDescription = "App Logo",
            modifier = Modifier
                .size(100.dp) // Chỉnh kích thước logo theo ý muốn
        )
        Spacer(modifier = Modifier.height(16.dp)) // Tạo khoảng cách với nội dung bên dưới

        Text("Chào mừng bạn đến với ứng dụng!", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Nội dung khác bên dưới
    }
}

@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    title: String,
    description: String,
    category: String,
    status: String,
    priority: String
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Thanh tiêu đề
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Blue)
            }
            Text(text = "Detail", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Blue, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            IconButton(onClick = { /* Xử lý xóa task */ }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }

        // Tiêu đề và mô tả task
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Text(text = description, fontSize = 16.sp, color = Color.Gray)

        // Thông tin trạng thái
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).background(Color(0xFFF5C5C5), shape = MaterialTheme.shapes.medium).padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TaskStatusItem("Category", category, Icons.Default.Work)
            TaskStatusItem("Status", status, Icons.Default.Build)
            TaskStatusItem("Priority", priority, Icons.Default.PriorityHigh)
        }

        // Danh sách subtasks
        Text(text = "Subtasks", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        val subtasks = listOf(
            "This task is related to Fitness. It needs to be completed",
            "This task is related to Fitness. It needs to be completed",
            "This task is related to Fitness. It needs to be completed"
        )
        subtasks.forEach { subtask -> TaskCheckboxItem(subtask) }

        // Phần đính kèm
        Text(text = "Attachments", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        AttachmentItem("document_1_0.pdf")
    }
}


// Hiển thị trạng thái task (Category, Status, Priority)
@Composable
fun TaskStatusItem(title: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = title)
        Text(text = title, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

// Checkbox cho subtask
@Composable
fun TaskCheckboxItem(text: String) {
    var checked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Text(text = text, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
    }
}

// Định dạng file đính kèm
@Composable
fun AttachmentItem(fileName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attachment")
        Text(text = fileName, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
    }
}







