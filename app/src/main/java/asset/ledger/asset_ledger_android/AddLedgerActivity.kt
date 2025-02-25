package asset.ledger.asset_ledger_android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import asset.ledger.asset_ledger_android.fragment.AssetDetailTypePopupFragment
import asset.ledger.asset_ledger_android.fragment.AssetTypeAddPopupFragment
import asset.ledger.asset_ledger_android.fragment.AssetTypePopupFragment
import asset.ledger.asset_ledger_android.fragment.UseCategoryPopupFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddLedgerActivity : AppCompatActivity() {

    private lateinit var dateTextView : TextView
    private lateinit var timeTextView : TextView
    private lateinit var amountEditText: EditText
    private lateinit var useCategoryEditText: EditText
    private lateinit var assetTypeEditText: EditText
    private lateinit var assetDetailTypeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var assetDetailTitleLinearLayout: LinearLayout
    private lateinit var assetDetailLinearLayout: LinearLayout
    private lateinit var assetTypeAddButton: Button
    private lateinit var assetDetailTypeAddButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ledger)

        dateTextView = findViewById(R.id.activity_add_ledger_date_text_view)
        timeTextView = findViewById(R.id.activity_add_ledger_time_text_view)
        amountEditText = findViewById(R.id.activity_add_ledger_amount_edit_text)
        useCategoryEditText = findViewById(R.id.activity_add_ledger_use_category_edit_text)
        assetDetailTitleLinearLayout = findViewById(R.id.activity_add_ledger_asset_detail_type_title_linear_layout)
        assetDetailLinearLayout = findViewById(R.id.activity_add_ledger_asset_detail_type_linear_layout)
        assetTypeEditText = findViewById(R.id.activity_add_ledger_asset_type_edit_text)
        assetDetailTypeEditText = findViewById(R.id.activity_add_ledger_asset_detail_type_edit_text)
        descriptionEditText = findViewById(R.id.activity_add_ledger_description_edit_text)
        assetTypeAddButton = findViewById(R.id.activity_add_ledger_asset_type_add_button)
        assetDetailTypeAddButton = findViewById(R.id.activity_add_ledger_asset_detail_type_add_button)

        setDateTextView()
        setDateTextViewClickListener()
        setTimeTextViewClickListener()
        setUseCategoryEditTextClickListener()
        setAssetTypeEditTextClickListener()
        setAssetDetailTypeEditTextClickListener()
        setAssetTypeAddButton()

    }

    private fun setAssetTypeAddButton() {
        assetTypeAddButton.setOnClickListener {
            val assetTypeAddPopupFragment = AssetTypeAddPopupFragment()
            assetTypeAddPopupFragment.show(supportFragmentManager, "assetTypeAddPopupFragment")
        }
    }

    fun updateAssetDetailTypeEditText(item: String) {
        assetDetailTypeEditText.setText(item)
    }

    private fun setAssetDetailTypeEditTextClickListener() {
        assetDetailTypeEditText.setOnClickListener {
            showAssetDetailTypePopup()
        }
    }

    private fun showAssetDetailTypePopup() {
        val assetType : String = assetTypeEditText.text.toString()

        val assetDetailTypePopupFragment = AssetDetailTypePopupFragment()

        val bundle = Bundle()
        bundle.putString("assetType", assetType)

        assetDetailTypePopupFragment.arguments = bundle

        assetDetailTypePopupFragment.show(supportFragmentManager, "assetDetailTypePopupFragment")
    }

    fun updateAssetTypeEditText(item: String) {
        assetTypeEditText.setText(item)

        assetDetailTypeEditText.text.clear()

        if (assetTypeEditText.text.toString() == "계좌" || assetTypeEditText.text.toString() == "카드") {
            assetDetailTitleLinearLayout.visibility = View.VISIBLE
            assetDetailLinearLayout.visibility = View.VISIBLE
        }
        else {
            assetDetailTitleLinearLayout.visibility = View.GONE
            assetDetailLinearLayout.visibility = View.GONE
        }
    }

    private fun setAssetTypeEditTextClickListener() {
        assetTypeEditText.setOnClickListener {
            showAssetTypePopup()
        }
    }

    private fun showAssetTypePopup() {
        val assetTypePopupFragment = AssetTypePopupFragment()
        assetTypePopupFragment.show(supportFragmentManager, "assetTypePopupFragment")
    }

    fun updateUseCategoryEditText(item: String) {
        useCategoryEditText.setText(item)
    }

    private fun setUseCategoryEditTextClickListener() {
        useCategoryEditText.setOnClickListener {
            showUseCategoryPopup()
        }
    }

    private fun showUseCategoryPopup() {
        val useCategoryPopupFragment = UseCategoryPopupFragment()
        useCategoryPopupFragment.show(supportFragmentManager, "useCategoryPopupFragment")
    }

    private fun setTimeTextViewClickListener() {
        // 현재 시간을 가져와서 시계의 기본값으로 설정
        timeTextView.setOnClickListener {
            val currentTime = timeTextView.text.toString()

            // 시간을 파싱하여 Calendar 객체로 변환
            val calendar = parseTimeToCalendar(currentTime)

            // TimePickerDialog에 초기 시간 설정
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // TimePickerDialog 생성
            val timePickerDialog = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfHour ->
                    // 사용자가 시간을 선택하면 TextView에 표시
                    val formattedTime = formatTime(hourOfDay, minuteOfHour)
                    timeTextView.text = formattedTime
                }, hour, minute, false // 12시간제
            )

            timePickerDialog.show()
        }
    }

    private fun parseTimeToCalendar(timeString: String): Calendar {
        val format = SimpleDateFormat("a h:mm", Locale.KOREAN)
        val time = format.parse(timeString) // "오후 6:19" 같은 형식
        val calendar = Calendar.getInstance()
        calendar.time = time
        return calendar
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val period = if (hourOfDay < 12) "오전" else "오후"

        // 12시간제 시간 포맷
        val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
        val timeFormat = SimpleDateFormat("h:mm", Locale.KOREAN)
        val time = timeFormat.format(Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }.time)

        return "$period $time"
    }

    private fun setDateTextViewClickListener() {
        dateTextView.setOnClickListener {
            val dateString = dateTextView.text.toString()
            val dateParts = dateString.split(" ")[0]  // "2025/02/19"을 얻음
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val date = dateFormat.parse(dateParts)  // String을 Date 객체로 변환

            // Date 객체에서 Calendar 가져오기
            val calendar = Calendar.getInstance()
            calendar.time = date

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // 날짜 선택 후 TextView에 표시
                    val selectedCalendar = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay)
                    }

                    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedCalendar.time)

                    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.KOREA)
                    val dayOfWeek = dayOfWeekFormat.format(selectedCalendar.time)

                    // 날짜와 요일을 TextView에 표시
                    dateTextView.text = "$formattedDate ($dayOfWeek)"

//                    val selectedDate = "$selectedYear/${String.format("%02d", selectedMonth + 1)}/$selectedDay" // 월은 0부터 시작하므로 +1
//                    dateTextView.text = selectedDate
                },
                year, month, day
            )

            datePickerDialog.show()
        }
    }

    private fun setDateTextView() {
        val now = Calendar.getInstance() // 현재 날짜와 시간

        // 날짜 포맷 (yyyy/MM/dd)
        val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN)
        val date = dateFormatter.format(now.time)

        // 요일 포맷 (화, 수, ... )
        val dayFormatter = SimpleDateFormat("EEE", Locale.KOREAN)
        val dayOfWeek = dayFormatter.format(now.time)

        // 오전/오후와 12시간제 시간
        val periodFormatter = SimpleDateFormat("a", Locale.KOREAN) // 오전/오후
        val period = periodFormatter.format(now.time)

        // 12시간제 시간과 분
        val timeFormatter = SimpleDateFormat("hh:mm", Locale.KOREAN)
        val time = timeFormatter.format(now.time)

        // 원하는 형식으로 반환
        dateTextView.text = "$date ($dayOfWeek)"
        timeTextView.text = "$period $time"
    }

}