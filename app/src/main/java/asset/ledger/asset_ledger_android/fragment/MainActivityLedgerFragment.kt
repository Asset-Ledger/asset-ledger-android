package asset.ledger.asset_ledger_android.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import asset.ledger.asset_ledger_android.R
import asset.ledger.asset_ledger_android.recyclerview.LedgerRecyclerViewAdapter
import asset.ledger.asset_ledger_android.recyclerview.LedgerRecyclerViewItem
import asset.ledger.asset_ledger_android.retrofit.ledger.LedgerApiInstance
import asset.ledger.asset_ledger_android.retrofit.ledger.dto.ResponseLedgerDto
import asset.ledger.asset_ledger_android.retrofit.ledger.dto.ResponseLedgerListDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.Query
import java.util.Calendar
import kotlin.math.min

class MainActivityLedgerFragment : Fragment() {

    private lateinit var yearMonthTextView : TextView
    private lateinit var assetTypeSpinner : Spinner
    private lateinit var assetDetailTypeSpinner : Spinner
    private lateinit var dateDueSpinner : Spinner
    private lateinit var useCategorySpinner : Spinner
    private lateinit var plusMinusSpinner : Spinner
    private lateinit var yearMonthLeftImageButton : ImageButton
    private lateinit var yearMonthRightImageButton : ImageButton
    private lateinit var initConditionButton : Button
    private lateinit var ledgerRecyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_activity_ledger, container, false)

        yearMonthTextView = view.findViewById(R.id.fragment_ledger_top_menu_year_month_text_view)
        assetTypeSpinner = view.findViewById(R.id.fragment_ledger_top_menu_asset_type_spinner)
        assetDetailTypeSpinner = view.findViewById(R.id.fragment_ledger_top_menu_asset_detail_type_spinner)
        dateDueSpinner = view.findViewById(R.id.fragment_ledger_top_menu_date_due_spinner)
        useCategorySpinner = view.findViewById(R.id.fragment_ledger_top_menu_use_category_spinner)
        plusMinusSpinner = view.findViewById(R.id.fragment_ledger_top_menu_plus_minus_spinner)
        yearMonthLeftImageButton = view.findViewById(R.id.fragment_ledger_top_menu_left_arrow_image_button)
        yearMonthRightImageButton = view.findViewById(R.id.fragment_ledger_top_menu_right_arrow_image_button)
        initConditionButton = view.findViewById(R.id.fragment_ledger_top_menu_init_condition_button)
        ledgerRecyclerView = view.findViewById(R.id.fragment_ledger_content_recycler_view)

        // yearMonthTextView 세팅
        setYearMonthText()

        // yearMonthImageButtom 세팅
        setYearMonthLeftImageButton()
        setYearMonthRightImageButton()

        // assetTypeSpinner 세팅
        setAssetTypeSpinner()
        setAssetTypeSpinnerClickListener()

        // dateDueSpinner 세팅
        setDateDueSpinner()

        // useCategorySpinner 세팅
        setUseCategorySpinner()

        // plusMinusSpinner 세팅
        setPlusMinusSpinner()

        // initButton 세팅
        setInitConditionButton()

//        setLedgerRecyclerView(ledgerRecyclerView)
        fetchLedgers(
            "user1",
            "1",
            ledgerRecyclerView
        )

        return view
    }

    // API 호출을 담당하는 비동기 함수
    private fun fetchLedgers(
        userId: String,
        startDate : String,
        ledgerRecyclerView: RecyclerView
    ) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetLedgerApiResponse(
                userId,
                startDate,
                ledgerRecyclerView
            )
        }
    }

    private suspend fun handleGetLedgerApiResponse(
        userId : String,
        startDate : String,
        ledgerRecyclerView: RecyclerView
    ) {
        try {
            val response = getLedgers(
                userId,
                startDate
            )

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val responseLedgerListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                println(response.toString())
                println(response.body().toString())

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseLedgerDtos : List<ResponseLedgerDto>? = responseLedgerListDto?.ledgerDtos
                    val ledgerRecyclerViewItems : List<LedgerRecyclerViewItem> = ledgerDtosToLedgerItems(responseLedgerDtos)

                    setLedgerRecyclerView(ledgerRecyclerView, ledgerRecyclerViewItems)
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "데이터를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
                setLedgerRecyclerView(ledgerRecyclerView, listOf())
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
            setLedgerRecyclerView(ledgerRecyclerView, listOf())
        }
    }

    private fun ledgerDtosToLedgerItems(responseLedgerDtos: List<ResponseLedgerDto>?) : List<LedgerRecyclerViewItem> {
        if (responseLedgerDtos.isNullOrEmpty()) {
            return listOf()
        }

        val ledgerDtosDatesAndTotalAmount : MutableMap<String, MutableList<Int>> = getLedgerDtosDatesAndTotalAmount(responseLedgerDtos)

        var ledgerRecyclerViewItem : MutableList<LedgerRecyclerViewItem> = mutableListOf()

        ledgerDtosDatesAndTotalAmount.forEach { dateAndAmount ->
            ledgerRecyclerViewItem.add(
                LedgerRecyclerViewItem(
                    "",
                    "",
                    "",
                    "",
                    0,
                    "",
                    dateAndAmount.key,
                    dateAndAmount.value[0],
                    dateAndAmount.value[1],
                    1
                )
            )
        }

        responseLedgerDtos.forEach { ledgerDto ->
            ledgerRecyclerViewItem.add(
                LedgerRecyclerViewItem(
                    ledgerDto.plusMinusType,
                    ledgerDto.useCategory,
                    ledgerDto.assetType,
                    ledgerDto.assetTypeDetail,
                    ledgerDto.amount,
                    ledgerDto.editTime,
                    ledgerDto.editDate,
                    0,
                    0,
                    0
                )
            )
        }

        return ledgerRecyclerViewItem.sortedWith (
            compareBy(
                {-createDateStringToInt(it.createDate)},
                {-it.itemType}
            )
        )
    }

    private fun createDateStringToInt(createDate : String) : Int {
        val createDateSplit : List<String> = createDate.split("/")

        if (createDateSplit.size != 3) {
            throw Exception("잘못된 날짜 형식 입니다.")
        }

        return "${createDateSplit[0]}${createDateSplit[1]}${createDateSplit[2]}".toInt()
    }

    private fun getLedgerDtosDatesAndTotalAmount(responseLedgerDtos: List<ResponseLedgerDto>?) : MutableMap<String, MutableList<Int>> {
        if (responseLedgerDtos.isNullOrEmpty()) {
            return mutableMapOf()
        }

        var datesAndPlusMinus : MutableMap<String, MutableList<Int>> = mutableMapOf()

        responseLedgerDtos.forEach { responseLedgerDto ->
            if (!datesAndPlusMinus.containsKey(responseLedgerDto.editDate)) {
                var plus : Int = 0
                var minus : Int = 0
                if (responseLedgerDto.plusMinusType.equals("PLUS") ||
                    responseLedgerDto.plusMinusType.equals("plus")
                    ) {
                    plus += responseLedgerDto.amount
                }
                else if (responseLedgerDto.plusMinusType.equals("MINUS") ||
                    responseLedgerDto.plusMinusType.equals("minus")
                    ) {
                    minus += responseLedgerDto.amount
                }

                datesAndPlusMinus.put(responseLedgerDto.editDate, mutableListOf(plus, minus))
            }
        }

        return datesAndPlusMinus
    }

    // Retrofit을 사용하여 API를 비동기적으로 호출
    private suspend fun getLedgers(
        userId: String,
        startDate : String,
    ): Response<ResponseLedgerListDto> {
        return withContext(Dispatchers.IO) {

            var assetDetailType : String = "전체"
            if (!assetTypeSpinner.selectedItem.toString().equals("전체") &&
                !assetTypeSpinner.selectedItem.toString().equals("계좌") &&
                !assetTypeSpinner.selectedItem.toString().equals("카드")) {
                assetDetailType = assetDetailTypeSpinner.selectedItem.toString()
            }
            LedgerApiInstance.ledgerApiService.getLedgers(
                userId,
                yearMonthTextView.text.toString(),
                startDate,
                plusMinusSpinner.selectedItem.toString(),
                useCategorySpinner.selectedItem.toString(),
                assetTypeSpinner.selectedItem.toString(),
                assetDetailType
            )
        }
    }

    private fun setLedgerRecyclerView(ledgerRecyclerView: RecyclerView, ledgerRecyclerViewItems : List<LedgerRecyclerViewItem>) {
        ledgerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ledgerRecyclerView.adapter = LedgerRecyclerViewAdapter(ledgerRecyclerViewItems)
    }

    private fun setInitConditionButton() {
        initConditionButton.setOnClickListener {
            setYearMonthText()
            assetTypeSpinner.setSelection(0)
            assetDetailTypeSpinner.setSelection(0)
            useCategorySpinner.setSelection(0)
            plusMinusSpinner.setSelection(0)
        }
    }

    private fun setYearMonthLeftImageButton() {
        yearMonthLeftImageButton.setOnClickListener {
            val yearMonth : List<String> = yearMonthTextView.text.toString().split("년 ")
            val currentYearString : String = yearMonth[0]
            val currentMonthString : String = yearMonth[1].split("월")[0]

            var currentYear : Int = currentYearString.toInt()
            var currentMonth : Int = currentMonthString.toInt()

            currentMonth -= 1
            if (currentMonth < 1) {
                currentYear -= 1
                currentMonth = 12
            }

            yearMonthTextView.setText("${currentYear}년 ${currentMonth}월")
            // retrofit 사용
            // 바뀐 년도, 월로 가계부 내역 조회
        }
    }

    private fun setYearMonthRightImageButton() {
        yearMonthRightImageButton.setOnClickListener {
            val yearMonth : List<String> = yearMonthTextView.text.toString().split("년 ")
            val currentYearString : String = yearMonth[0]
            val currentMonthString : String = yearMonth[1].split("월")[0]

            var currentYear : Int = currentYearString.toInt()
            var currentMonth : Int = currentMonthString.toInt()

            currentMonth += 1
            if (currentMonth > 12) {
                currentYear += 1
                currentMonth = 1
            }

            yearMonthTextView.setText("${currentYear}년 ${currentMonth}월")
            // retrofit 사용
            // 바뀐 년도, 월로 가계부 내역 조회
        }
    }

    private fun setYearMonthText() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        yearMonthTextView.setText("${year}년 ${month}월")
    }

    private fun setPlusMinusSpinner() {
        val plusMinus : List<String> = listOf("전체", "수입", "지출")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, plusMinus)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        plusMinusSpinner.adapter = adapter

        if (plusMinus.isNotEmpty()) {
            plusMinusSpinner.setSelection(0)
        }
    }

    private fun setUseCategorySpinner() {
        val useCategory = getUseCategories()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, useCategory)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        useCategorySpinner.adapter = adapter

        if (useCategory.isNotEmpty()) {
            useCategorySpinner.setSelection(0)
        }
    }

    private fun getUseCategories() : List<String> {
        // retrofit 사용
        // 서버에서 useCategory 정보 가져와서 넣어주기
        return listOf("전체", "식비", "교통비", "운동")
    }

    private fun setDateDueSpinner() {
        val dateDue : List<String> = listOf("일일", "달력")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateDue)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateDueSpinner.adapter = adapter

        if (dateDue.isNotEmpty()) {
            dateDueSpinner.setSelection(0)
        }
    }

    private fun setAssetDetailTypeSpinner(assetDetailTypeSpinner : Spinner, assetType : String) {
        val assetDetailTypes = getAssetDetailTypes(assetType)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assetDetailTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assetDetailTypeSpinner.adapter = adapter

        if (assetDetailTypes.isNotEmpty()) {
            assetDetailTypeSpinner.setSelection(0)
        }
    }

    private fun getAssetDetailTypes(assetType : String) : List<String> {
        // retrofit 사용
        // 서버에서 assetDetailType 정보 가져와서 넣어주기
        return listOf("전체", "하나은행", "국민은행", "카카오뱅크", "신한은행", "우리은행")
    }

    private fun setAssetTypeSpinner() {
        val assetTypes = getAssetTypes()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assetTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assetTypeSpinner.adapter = adapter

        if (assetTypes.isNotEmpty()) {
            assetTypeSpinner.setSelection(0)
        }
    }

    private fun setAssetTypeSpinnerClickListener() {
        assetTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 항목 처리
                val selectedItem = parentView?.getItemAtPosition(position).toString()

                // assetType이 계좌 or 카드일 경우 assetDetailTypeSpinner visible 및 세팅
                if (selectedItem.equals("계좌") or selectedItem.equals("카드")) {
                    assetDetailTypeSpinner.visibility = View.VISIBLE

                    setAssetDetailTypeSpinner(assetDetailTypeSpinner, selectedItem)
                }
                else {
                    assetDetailTypeSpinner.visibility = View.INVISIBLE
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }
    }

    private fun getAssetTypes() : List<String> {
        // retrofit 사용
        // 서버에서 assetType 정보 가져와서 넣어주기
        return listOf("전체", "계좌", "카드", "현금")
    }

}