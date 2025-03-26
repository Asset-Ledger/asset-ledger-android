package asset.ledger.asset_ledger_android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import asset.ledger.asset_ledger_android.fragment.AssetDetailTypeAddPopupFragment
import asset.ledger.asset_ledger_android.fragment.AssetDetailTypePopupFragment
import asset.ledger.asset_ledger_android.fragment.AssetTypeAddPopupFragment
import asset.ledger.asset_ledger_android.fragment.AssetTypePopupFragment
import asset.ledger.asset_ledger_android.fragment.UseCategoryAddPopupFragment
import asset.ledger.asset_ledger_android.fragment.UseCategoryPopupFragment
import asset.ledger.asset_ledger_android.recyclerview.LedgerRecyclerViewItem
import asset.ledger.asset_ledger_android.retrofit.ledger.LedgerApiInstance
import asset.ledger.asset_ledger_android.retrofit.ledger.dto.RequestLedgerDto
import asset.ledger.asset_ledger_android.retrofit.ledger.dto.RequestTransferLedgerDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddLedgerActivity : AppCompatActivity() {

    private lateinit var plusMinusRadioGroup: RadioGroup

    private lateinit var dateTextView : TextView
    private lateinit var timeTextView : TextView

    private lateinit var amountEditText: EditText
    private lateinit var useCategoryEditText: EditText
    private lateinit var assetTypeEditText: EditText
    private lateinit var assetDetailTypeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var assetOutAccountEditText: EditText
    private lateinit var assetInAccountEditText: EditText

    private lateinit var useCategoryTitleLinearLayout: LinearLayout
    private lateinit var useCategoryLinearLayout: LinearLayout
    private lateinit var assetTypeTitleLinearLayout: LinearLayout
    private lateinit var assetTypeLinearLayout: LinearLayout
    private lateinit var assetDetailTypeTitleLinearLayout: LinearLayout
    private lateinit var assetDetailTypeLinearLayout: LinearLayout
    private lateinit var assetOutAccountTitleLinearLayout: LinearLayout
    private lateinit var assetOutAccountLinearLayout: LinearLayout
    private lateinit var assetInAccountTitleLinearLayout: LinearLayout
    private lateinit var assetInAccountLinearLayout: LinearLayout

    private lateinit var useCategoryAddButton: Button
    private lateinit var assetTypeAddButton: Button
    private lateinit var assetDetailTypeAddButton: Button
    private lateinit var assetOutAccountAddButton: Button
    private lateinit var assetInAccountAddButton: Button

    private lateinit var ledgerSaveButton: Button
    private lateinit var ledgerAdditionalCreateButton: Button

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ledger)

        plusMinusRadioGroup = findViewById(R.id.activity_add_ledger_radio_group)
        dateTextView = findViewById(R.id.activity_add_ledger_date_text_view)
        timeTextView = findViewById(R.id.activity_add_ledger_time_text_view)
        amountEditText = findViewById(R.id.activity_add_ledger_amount_edit_text)
        useCategoryEditText = findViewById(R.id.activity_add_ledger_use_category_edit_text)

        useCategoryTitleLinearLayout = findViewById(R.id.activity_add_ledger_use_category_title_linear_layout)
        useCategoryLinearLayout = findViewById(R.id.activity_add_ledger_use_category_linear_layout)
        assetTypeTitleLinearLayout = findViewById(R.id.activity_add_ledger_asset_type_title_linear_layout)
        assetTypeLinearLayout = findViewById(R.id.activity_add_ledger_asset_type_linear_layout)
        assetDetailTypeTitleLinearLayout = findViewById(R.id.activity_add_ledger_asset_detail_type_title_linear_layout)
        assetDetailTypeLinearLayout = findViewById(R.id.activity_add_ledger_asset_detail_type_linear_layout)
        assetOutAccountTitleLinearLayout = findViewById(R.id.activity_add_ledger_out_account_title_linear_layout)
        assetOutAccountLinearLayout = findViewById(R.id.activity_add_ledger_out_account_linear_layout)
        assetInAccountTitleLinearLayout = findViewById(R.id.activity_add_ledger_in_account_title_linear_layout)
        assetInAccountLinearLayout = findViewById(R.id.activity_add_ledger_in_account_linear_layout)

        assetTypeEditText = findViewById(R.id.activity_add_ledger_asset_type_edit_text)
        assetDetailTypeEditText = findViewById(R.id.activity_add_ledger_asset_detail_type_edit_text)
        assetOutAccountEditText = findViewById(R.id.activity_add_ledger_out_account_edit_text)
        assetInAccountEditText = findViewById(R.id.activity_add_ledger_in_account_edit_text)
        descriptionEditText = findViewById(R.id.activity_add_ledger_description_edit_text)

        useCategoryAddButton = findViewById(R.id.activity_add_ledger_use_category_add_button)
        assetTypeAddButton = findViewById(R.id.activity_add_ledger_asset_type_add_button)
        assetDetailTypeAddButton = findViewById(R.id.activity_add_ledger_asset_detail_type_add_button)
        assetOutAccountAddButton = findViewById(R.id.activity_add_ledger_out_account_add_button)
        assetInAccountAddButton = findViewById(R.id.activity_add_ledger_in_account_add_button)
        ledgerSaveButton = findViewById(R.id.activity_add_ledger_save_save_button)
        ledgerAdditionalCreateButton = findViewById(R.id.activity_add_ledger_additional_create_button)

        val userId: String = "user1"
        val isUpdate : Boolean = intent.getBooleanExtra("isUpdate", false)

        hideLinearLayout()
        setDateTextView()
        setDateTextViewClickListener()
        setTimeTextViewClickListener()
        setUseCategoryEditTextClickListener()
        setAssetTypeEditTextClickListener()
        setAssetDetailTypeEditTextClickListener()
        setAssetOutAccountEditTextClickListener()
        setAssetInAccountEditTextClickListener()
        setUseCategoryAddButton()
        setAssetTypeAddButton()
        setAssetDetailTypeAddButton()
        setAssetOutAccountAddButtonClickListener()
        setAssetInAccountAddButtonClickListener()
        setLedgerSaveButton(userId, isUpdate)
        setRadioGroupCheckedChangeListener()

        if (isUpdate) {
            val ledgerRecyclerViewItem = intent.getParcelableExtra("currentLedgerRecyclerViewItem", LedgerRecyclerViewItem::class.java)
            setUpdateLedgerInfo(ledgerRecyclerViewItem, userId)
        }

    }

    private fun setUpdateLedgerInfo(ledgerRecyclerViewItem : LedgerRecyclerViewItem?, userId: String) {
        if (ledgerRecyclerViewItem == null) {
            Toast.makeText(this@AddLedgerActivity, "수정할 가계부를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            return
        }

        if (ledgerRecyclerViewItem?.plusMinus?.uppercase().equals("PLUS")) {
            val radioButton : RadioButton = findViewById(R.id.activity_add_ledger_plus_radio_button)
            radioButton.isChecked = true
        } else if (ledgerRecyclerViewItem?.plusMinus?.uppercase().equals("MINUS")) {
            val radioButton: RadioButton = findViewById(R.id.activity_add_ledger_minus_radio_button)
            radioButton.isChecked = true
        }

        val transferRadioButton: RadioButton = findViewById(R.id.activity_add_ledger_transfer_radio_button)
        transferRadioButton.isEnabled = false

        dateTextView.text = changeDateFormat(ledgerRecyclerViewItem.createDate)
        timeTextView.text = changeTimeFormat(ledgerRecyclerViewItem.createdTime)
        amountEditText.setText(ledgerRecyclerViewItem.amount.toString())
        updateUseCategoryEditText(ledgerRecyclerViewItem.useCategory)
        updateAssetTypeEditText(ledgerRecyclerViewItem.assetType)
        if (assetTypeEditText.text.toString() == "계좌" || assetTypeEditText.text.toString() == "카드") {
            assetDetailTypeEditText.setText(ledgerRecyclerViewItem.assetDetailType)
        }

        descriptionEditText.setText(ledgerRecyclerViewItem.description)
    }

    fun changeDateFormat(currentDate: String): String {
        // 입력된 날짜 문자열을 SimpleDateFormat을 사용하여 Date 객체로 파싱
        val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN)
        val date: Date = inputFormat.parse(currentDate)

        // SimpleDateFormat을 사용하여 날짜를 "yyyy/MM/dd" 형식으로 포맷
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN)
        val formattedDate = outputFormat.format(date)

        // Calendar를 사용하여 요일을 구하기
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.KOREAN)

        // 결과 반환 (요일 추가)
        return "$formattedDate (${dayOfWeek.split("요일")[0]})"
    }

    fun changeTimeFormat(currentTime: String): String {
        // 입력된 시간 문자열을 SimpleDateFormat을 사용하여 Date 객체로 변환
        val inputFormat = SimpleDateFormat("a h:mm:ss", Locale.KOREAN)  // "a"는 AM/PM, "h"는 12시간제 시간
        val date = inputFormat.parse(currentTime)

        // 변환된 Date 객체를 원하는 출력 형식으로 포맷
        val outputFormat = SimpleDateFormat("a hh:mm", Locale.KOREAN)  // "hh"는 두 자릿수 시간
        return outputFormat.format(date)
    }

    private fun setAssetOutAccountAddButtonClickListener() {
        assetOutAccountAddButton.setOnClickListener {
            val assetType : String = "계좌"

            val assetDetailTypeAddPopupFragment = AssetDetailTypeAddPopupFragment()

            val bundle = Bundle()
            bundle.putString("assetType", assetType)

            assetDetailTypeAddPopupFragment.arguments = bundle

            assetDetailTypeAddPopupFragment.show(supportFragmentManager, "assetDetailTypeAddPopupFragment")
        }
    }

    private fun setAssetInAccountAddButtonClickListener() {
        assetInAccountAddButton.setOnClickListener {
            val assetType : String = "계좌"

            val assetDetailTypeAddPopupFragment = AssetDetailTypeAddPopupFragment()

            val bundle = Bundle()
            bundle.putString("assetType", assetType)

            assetDetailTypeAddPopupFragment.arguments = bundle

            assetDetailTypeAddPopupFragment.show(supportFragmentManager, "assetDetailTypeAddPopupFragment")
        }
    }

    private fun setAssetOutAccountEditTextClickListener() {
        assetOutAccountEditText.setOnClickListener {
            showAssetDetailTypePopup(true, R.id.activity_add_ledger_out_account_edit_text)
        }
    }

    private fun setAssetInAccountEditTextClickListener() {
        assetInAccountEditText.setOnClickListener {
            showAssetDetailTypePopup(true, R.id.activity_add_ledger_in_account_edit_text)
        }
    }

    private fun setRadioGroupCheckedChangeListener() {
        plusMinusRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.activity_add_ledger_transfer_radio_button -> {
                    checkTransfer()
                }
                else -> {
                    checkPlusMinus()
                }
            }
        }
    }

    private fun checkTransfer() {
        useCategoryTitleLinearLayout.visibility = View.GONE
        useCategoryLinearLayout.visibility = View.GONE
        assetTypeTitleLinearLayout.visibility = View.GONE
        assetTypeLinearLayout.visibility = View.GONE
        assetDetailTypeTitleLinearLayout.visibility = View.GONE
        assetDetailTypeLinearLayout.visibility = View.GONE
        useCategoryEditText.text.clear()
        assetTypeEditText.text.clear()
        assetDetailTypeEditText.text.clear()

        assetOutAccountTitleLinearLayout.visibility = View.VISIBLE
        assetOutAccountLinearLayout.visibility = View.VISIBLE
        assetInAccountTitleLinearLayout.visibility = View.VISIBLE
        assetInAccountLinearLayout.visibility = View.VISIBLE
    }

    private fun checkPlusMinus() {
        useCategoryTitleLinearLayout.visibility = View.VISIBLE
        useCategoryLinearLayout.visibility = View.VISIBLE
        assetTypeTitleLinearLayout.visibility = View.VISIBLE
        assetTypeLinearLayout.visibility = View.VISIBLE

        assetOutAccountTitleLinearLayout.visibility = View.GONE
        assetOutAccountLinearLayout.visibility = View.GONE
        assetInAccountTitleLinearLayout.visibility = View.GONE
        assetInAccountLinearLayout.visibility = View.GONE
        assetOutAccountEditText.text.clear()
        assetInAccountEditText.text.clear()
    }

    private fun setLedgerSaveButton(userId: String, isUpdate: Boolean) {
        if (isUpdate) {
            ledgerSaveButton.text = "수정"
            ledgerAdditionalCreateButton.isEnabled = false

            ////

        }
        else {
            ledgerSaveButton.setOnClickListener {
                fetchCreateLedger(userId)
            }
        }


    }

    private fun hideLinearLayout() {
        assetDetailTypeTitleLinearLayout.visibility = View.GONE
        assetDetailTypeLinearLayout.visibility = View.GONE
        assetOutAccountTitleLinearLayout.visibility = View.GONE
        assetOutAccountLinearLayout.visibility = View.GONE
        assetInAccountTitleLinearLayout.visibility = View.GONE
        assetInAccountLinearLayout.visibility = View.GONE
    }

    private fun setAssetDetailTypeAddButton() {
        assetDetailTypeAddButton.setOnClickListener {
            val assetType : String = assetTypeEditText.text.toString()

            if (assetType.isNullOrEmpty()) {
                Toast.makeText(this, "자산 종류를 선택해주세요.", Toast.LENGTH_LONG).show()
            }

            val assetDetailTypeAddPopupFragment = AssetDetailTypeAddPopupFragment()

            val bundle = Bundle()
            bundle.putString("assetType", assetType)

            assetDetailTypeAddPopupFragment.arguments = bundle

            assetDetailTypeAddPopupFragment.show(supportFragmentManager, "assetDetailTypeAddPopupFragment")
        }
    }

    private fun setAssetTypeAddButton() {
        assetTypeAddButton.setOnClickListener {
            val assetTypeAddPopupFragment = AssetTypeAddPopupFragment()
            assetTypeAddPopupFragment.show(supportFragmentManager, "assetTypeAddPopupFragment")
        }
    }

    private fun setUseCategoryAddButton() {
        useCategoryAddButton.setOnClickListener {
            val assetTypeAddPopupFragment = UseCategoryAddPopupFragment()
            assetTypeAddPopupFragment.show(supportFragmentManager, "useCategoryAddPopupFragment")
        }
    }

    fun updateOutAccountEditText(item: String) {
        assetOutAccountEditText.setText(item)
    }

    fun updateInAccountEditText(item: String) {
        assetInAccountEditText.setText(item)
    }

    fun updateAssetDetailTypeEditText(item: String) {
        assetDetailTypeEditText.setText(item)
    }

    private fun setAssetDetailTypeEditTextClickListener() {
        assetDetailTypeEditText.setOnClickListener {
            showAssetDetailTypePopup(false, R.id.activity_add_ledger_asset_detail_type_edit_text)
        }
    }

    private fun showAssetDetailTypePopup(isTransfer: Boolean, editTextId: Int) {
        val assetType : String = assetTypeEditText.text.toString()

        val assetDetailTypePopupFragment = AssetDetailTypePopupFragment(editTextId)

        val bundle = Bundle()

        if (isTransfer) {
            bundle.putString("assetType", "계좌")
        }
        else {
            bundle.putString("assetType", assetType)
        }

        assetDetailTypePopupFragment.arguments = bundle

        assetDetailTypePopupFragment.show(supportFragmentManager, "assetDetailTypePopupFragment")
    }

    fun updateAssetTypeEditText(item: String) {
        assetTypeEditText.setText(item)

        assetDetailTypeEditText.text.clear()

        if (assetTypeEditText.text.toString() == "계좌" || assetTypeEditText.text.toString() == "카드") {
            assetDetailTypeTitleLinearLayout.visibility = View.VISIBLE
            assetDetailTypeLinearLayout.visibility = View.VISIBLE
        }
        else {
            assetDetailTypeTitleLinearLayout.visibility = View.GONE
            assetDetailTypeLinearLayout.visibility = View.GONE
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

    private fun getPlusMinus(): String {
        var plusMinus: String = "PLUS"
        val selectedRadioButtonId = plusMinusRadioGroup.checkedRadioButtonId
        val plusMinusText = findViewById<Button>(selectedRadioButtonId).text.toString()

        if (plusMinusText.equals("지출")) {
            plusMinus = "MINUS"
        }
        else if (plusMinusText.equals("이체")) {
            plusMinus = "TRANSFER"
        }

        return plusMinus
    }

    private fun buildRequestTransferOutLedgerDto(): RequestLedgerDto {
        var description: String = ""

        if (!descriptionEditText.text.toString().isNullOrEmpty()) {
            description = descriptionEditText.text.toString()
        }

        // 출금 계좌 Ledger 생성
        val requestOutLedgerDto: RequestLedgerDto = RequestLedgerDto(
            "MINUS",
            dateTextView.text.toString(),
            timeTextView.text.toString(),
            "출금 이체",
            "계좌",
            assetOutAccountEditText.text.toString(),
            description,
            amountEditText.text.toString().toInt()
        )

        return requestOutLedgerDto
    }

    private fun buildRequestTransferInLedgerDto(): RequestLedgerDto {
        var description: String = ""

        if (!descriptionEditText.text.toString().isNullOrEmpty()) {
            description = descriptionEditText.text.toString()
        }

        // 입금 계좌 Ledger 생성
        val requestInLedgerDto: RequestLedgerDto = RequestLedgerDto(
            "PLUS",
            dateTextView.text.toString(),
            timeTextView.text.toString(),
            "입금 이체",
            "계좌",
            assetInAccountEditText.text.toString(),
            description,
            amountEditText.text.toString().toInt()
        )

        return requestInLedgerDto
    }

    private fun buildRequestPlusMinusLedgerDto(): RequestLedgerDto {
        var plusMinus: String = getPlusMinus()

        var description: String = ""

        if (!descriptionEditText.text.toString().isNullOrEmpty()) {
            description = descriptionEditText.text.toString()
        }

        val requestLedgerDto: RequestLedgerDto = RequestLedgerDto(
            plusMinus,
            dateTextView.text.toString(),
            timeTextView.text.toString(),
            useCategoryEditText.text.toString(),
            assetTypeEditText.text.toString(),
            assetDetailTypeEditText.text.toString(),
            description,
            amountEditText.text.toString().toInt()
        )

        return requestLedgerDto
    }

    private fun validateTransferLedgerRequest(): Boolean {
        if (amountEditText.text.toString().isNullOrEmpty()) {
            return false
        }
        else {
            if (amountEditText.text.toString().toIntOrNull() == null) {
                return false
            }
        }

        if (assetOutAccountEditText.text.toString().isNullOrEmpty()) {
            return false
        }

        if (assetInAccountEditText.text.toString().isNullOrEmpty()) {
            return false
        }

        return true
    }

    private fun validatePlusMinusLedgerRequest(): Boolean {
        if (amountEditText.text.toString().isNullOrEmpty()) {
            return false
        }
        else {
            if (amountEditText.text.toString().toIntOrNull() == null) {
                return false
            }
        }

        if (useCategoryEditText.text.toString().isNullOrEmpty()) {
            return false
        }

        if (assetTypeEditText.text.toString().isNullOrEmpty()) {
            return false
        }

        if (assetTypeEditText.text.toString().equals("계좌")
            || assetTypeEditText.text.toString().equals("카드")) {
            if (assetDetailTypeEditText.text.toString().isNullOrEmpty()) {
                return false
            }
        }

        return true
    }

    private fun fetchCreateLedger(userId: String) {
        val pluMinus = getPlusMinus()

        if (!pluMinus.equals("TRANSFER")) {
            // 이체가 아닐 경우, 즉 수입이나 지출일 경우
            fetchCreatePlusMinusLedger(userId)
        }
        else {
            // 이체인 경우
            fetchCreateTransferLedger(userId)
        }
    }

    private fun fetchCreateTransferLedger(userId: String) {
        if (!validateTransferLedgerRequest()) {
            Toast.makeText(this, "필요한 내용들히 정확히 입력해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        if (assetOutAccountEditText.text.toString().equals(assetInAccountEditText.text.toString())) {
            Toast.makeText(this, "출금 계좌와 입금 계좌는 같을 수 없습니다.", Toast.LENGTH_LONG).show()
            return
        }

        val requestOutLedgerDto: RequestLedgerDto = buildRequestTransferOutLedgerDto()
        val requestInLedgerDto: RequestLedgerDto = buildRequestTransferInLedgerDto()

        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleCreateTransferLedgerApiResponse(
                userId,
                requestOutLedgerDto,
                requestInLedgerDto
            )
        }
    }

    private fun fetchCreatePlusMinusLedger(userId: String) {
        if (!validatePlusMinusLedgerRequest()) {
            Toast.makeText(this, "필요한 내용들을 정확 입력해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        val requestLedgerDto: RequestLedgerDto = buildRequestPlusMinusLedgerDto()

        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleCreatePlusMinusLedgerApiResponse(
                userId,
                requestLedgerDto
            )
        }
    }

    private suspend fun handleCreateTransferLedgerApiResponse(
        userId : String,
        requestOutLedgerDto: RequestLedgerDto,
        requestInLedgerDto: RequestLedgerDto
    ) {
        try {
            val response = createTransferLedger(userId, requestOutLedgerDto, requestInLedgerDto)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                Toast.makeText(this@AddLedgerActivity, "정상적으로 이체 추가를 완료했습니다.", Toast.LENGTH_LONG).show()

                // UI 업데이트 (Main 스레드에서 처리)
//                withContext(Dispatchers.Main) {
//                    finish()
//                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddLedgerActivity, "이체 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddLedgerActivity, "이체 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun handleCreatePlusMinusLedgerApiResponse(userId : String, requestLedgerDto: RequestLedgerDto) {
        try {
            val response = createPlusMinusLedger(userId, requestLedgerDto)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                Toast.makeText(this@AddLedgerActivity, "정상적으로 가계부 추가를 완료했습니다.", Toast.LENGTH_LONG).show()

//                 UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    finish()
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddLedgerActivity, "가계부 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddLedgerActivity, "가계부 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun createTransferLedger(
        userId: String,
        requestOutLedgerDto: RequestLedgerDto,
        requestInLedgerDto: RequestLedgerDto
    ): Response<Void> {
        return withContext(Dispatchers.IO) {
            LedgerApiInstance.ledgerApiService.createTransferLedger(
                userId,
                RequestTransferLedgerDto(requestOutLedgerDto, requestInLedgerDto)
            )
        }
    }

    private suspend fun createPlusMinusLedger(userId: String, requestLedgerDto: RequestLedgerDto): Response<Void> {
        return withContext(Dispatchers.IO) {
            LedgerApiInstance.ledgerApiService.createPlusMinusLedger(userId, requestLedgerDto)
        }
    }

}