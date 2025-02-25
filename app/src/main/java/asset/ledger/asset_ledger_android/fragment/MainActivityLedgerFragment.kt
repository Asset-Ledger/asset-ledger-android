package asset.ledger.asset_ledger_android.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.core.view.size
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import asset.ledger.asset_ledger_android.AddLedgerActivity
import asset.ledger.asset_ledger_android.R
import asset.ledger.asset_ledger_android.recyclerview.LedgerRecyclerViewAdapter
import asset.ledger.asset_ledger_android.recyclerview.LedgerRecyclerViewItem
import asset.ledger.asset_ledger_android.retrofit.asset.AssetApiInstance
import asset.ledger.asset_ledger_android.retrofit.asset.dto.ResponseAssetDto
import asset.ledger.asset_ledger_android.retrofit.asset.dto.ResponseAssetListDto
import asset.ledger.asset_ledger_android.retrofit.assetDetail.AssetDetailApiInstance
import asset.ledger.asset_ledger_android.retrofit.assetDetail.AssetDetailApiService
import asset.ledger.asset_ledger_android.retrofit.assetDetail.dto.ResponseAssetDetailDto
import asset.ledger.asset_ledger_android.retrofit.assetDetail.dto.ResponseAssetDetailListDto
import asset.ledger.asset_ledger_android.retrofit.category.UseCategoryApiInstance
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryDto
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryListDto
import asset.ledger.asset_ledger_android.retrofit.ledger.LedgerApiInstance
import asset.ledger.asset_ledger_android.retrofit.ledger.dto.ResponseLedgerDto
import asset.ledger.asset_ledger_android.retrofit.ledger.dto.ResponseLedgerListDto
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var searchLedgerButton : Button
    private lateinit var addLedgerFloatingActionButton : FloatingActionButton
    private var assetTypeApiFlag : Boolean = false
    private var useCategoryApiFlag : Boolean = false

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
        searchLedgerButton = view.findViewById(R.id.fragment_ledger_top_menu_search_button)
        addLedgerFloatingActionButton = view.findViewById(R.id.fragment_ledger_top_menu_add_ledger_floating_action_button)

        val userId : String = "user1"
        val startDate : String = "1"

        // yearMonthTextView 세팅
        setYearMonthText()

        // yearMonthImageButtom 세팅
        setYearMonthLeftImageButton()
        setYearMonthRightImageButton()

        setAssetTypeSpinnerClickListener(userId, startDate)

        setAssetDetailTypeSpinnerInit()

        // dateDueSpinner 세팅
        setDateDueSpinner()

        // plusMinusSpinner 세팅
        setPlusMinusSpinner()

        // initButton 세팅
        setInitConditionButton()

        // addLedgerFloatingActionButton 세팅
        setAddLedgerFloatingActionButton()

        // searchLedgerButton 세팅
        setSearchLedgerButton(userId, startDate)

        // assetType 데이터 호출하고 assetTypeSpinner 세팅
        fetchGetAssets(userId, startDate)

        // useCategory 데이터 호출하고 useCategorySpinner 세팅
        fetchGetUseCategories(userId, startDate)

        return view
    }

    // API 호출을 담당하는 비동기 함수
    private fun fetchGetLedgers(
        userId: String,
        startDate : String
    ) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetLedgerApiResponse(
                userId,
                startDate
            )
        }
    }

    private suspend fun handleGetLedgerApiResponse(
        userId : String,
        startDate : String
    ) {
        try {
            println("fffffffffffff 호출")
            val response = getLedgers(
                userId,
                startDate
            )

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val responseLedgerListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseLedgerDtos : List<ResponseLedgerDto>? = responseLedgerListDto?.ledgerDtos
                    val ledgerRecyclerViewItems : List<LedgerRecyclerViewItem> = ledgerDtosToLedgerItems(responseLedgerDtos)

                    setLedgerRecyclerView(ledgerRecyclerViewItems)
                }

            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "데이터를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
                setLedgerRecyclerView(listOf())
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
            println(e)
            setLedgerRecyclerView(listOf())
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
                datesAndPlusMinus.put(responseLedgerDto.editDate, mutableListOf(0, 0))
            }

            var plusAndMinusAmount : MutableList<Int>? = datesAndPlusMinus.get(responseLedgerDto.editDate) ?: mutableListOf(0, 0)
            var plus : Int = plusAndMinusAmount?.get(0) ?: 0
            var minus : Int = plusAndMinusAmount?.get(1) ?: 0

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

    private fun setLedgerRecyclerView(ledgerRecyclerViewItems : List<LedgerRecyclerViewItem>) {
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

        initConditionButton.isEnabled = false
    }

    private fun setAddLedgerFloatingActionButton() {
        addLedgerFloatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), AddLedgerActivity::class.java)
            startActivity(intent)
        }

        addLedgerFloatingActionButton.isEnabled = false
    }

    private fun setSearchLedgerButton(userId : String, startDate: String) {
        searchLedgerButton.setOnClickListener {
            fetchGetLedgers(userId, startDate)
        }

        searchLedgerButton.isEnabled = false
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
        yearMonthLeftImageButton.isEnabled = false
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

        yearMonthRightImageButton.isEnabled = false
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

        plusMinusSpinner.isEnabled = false
    }

    private fun setUseCategorySpinner(useCategories : List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, useCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        useCategorySpinner.adapter = adapter

        useCategorySpinner.isEnabled = false
    }

    private fun setDateDueSpinner() {
        val dateDue : List<String> = listOf("일일", "달력")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateDue)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateDueSpinner.adapter = adapter

        dateDueSpinner.isEnabled = false
    }

    private fun setAssetDetailTypeSpinnerInit() {
        val initList : List<String> = listOf("전체")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, initList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assetDetailTypeSpinner.adapter = adapter

        assetDetailTypeSpinner.isEnabled = false
    }

    private fun setAssetDetailTypeSpinner(assetDetailTypes : List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assetDetailTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assetDetailTypeSpinner.adapter = adapter

    }

    private fun setAssetTypeSpinner(assetTypes : List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assetTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assetTypeSpinner.adapter = adapter

        assetTypeSpinner.isEnabled = false
    }

    private fun fetchGetAssets(userId: String, startDate: String) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetAssetApiResponse(userId, startDate)
        }
    }

    private suspend fun handleGetAssetApiResponse(userId : String, startDate: String) {
        try {
            val response = getAssets(userId)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                assetTypeApiFlag = true
                val responseAssetListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseAssetDtos : List<ResponseAssetDto>? = responseAssetListDto?.assetDtos
                    val assetTypes : MutableList<String> = mutableListOf("전체")

                    if (responseAssetDtos != null){
                        responseAssetDtos.forEach { responseAssetDto ->
                            assetTypes.add(responseAssetDto.assetType)
                        }
                    }

                    setAssetTypeSpinner(assetTypes)

                    if (useCategoryApiFlag) {
                        fetchGetLedgers(userId, startDate)
                        setSpinnersAndButtonsEnable()
                    }
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "자산 종류를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }

                setAssetTypeSpinner(listOf("전체"))
                setAssetTypeSpinnerClickListener(userId, startDate)
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "자산 종류를 불러오는 과정에서 네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }

            setAssetTypeSpinner(listOf("전체"))
            setAssetTypeSpinnerClickListener(userId, startDate)
        }
    }

    private suspend fun getAssets(userId: String): Response<ResponseAssetListDto> {
        return withContext(Dispatchers.IO) {
            AssetApiInstance.assetApiService.getAssets(userId)
        }
    }

    private fun setAssetTypeSpinnerClickListener(userId: String, startDate: String) {
        assetTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 항목 처리
                val selectedItem = parentView?.getItemAtPosition(position).toString()

                // assetType이 계좌 or 카드일 경우 assetDetailTypeSpinner visible 및 세팅
                if (selectedItem.equals("계좌") or selectedItem.equals("카드")) {

                    // retrofit
                    fetchGetAssetDetailTypes(userId, selectedItem)
                    assetDetailTypeSpinner.isEnabled = true

                }
                else {
                    assetDetailTypeSpinner.isEnabled = false
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }
    }

    private fun fetchGetUseCategories(userId: String, startDate: String) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetUseCategoryApiResponse(userId, startDate)
        }
    }

    private suspend fun handleGetUseCategoryApiResponse(userId : String, startDate: String) {
        try {
            val response = getUseCategories(userId)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                useCategoryApiFlag = true
                val responseUseCategoryListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseUseCategoryDtos : List<ResponseUseCategoryDto>? = responseUseCategoryListDto?.useCategoryDtos
                    val useCategories : MutableList<String> = mutableListOf("전체")

                    println(responseUseCategoryDtos?.get(0))
                    if (responseUseCategoryDtos != null){
                        responseUseCategoryDtos.forEach { responseUseCategory ->
                            useCategories.add(responseUseCategory.useCategory)
                        }
                    }

                    setUseCategorySpinner(useCategories)

                    if (assetTypeApiFlag) {
                        fetchGetLedgers(userId, startDate)
                        setSpinnersAndButtonsEnable()
                    }
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }

                setUseCategorySpinner(listOf("전체"))
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }

            setUseCategorySpinner(listOf("전체"))
        }
    }

    private suspend fun getUseCategories(userId: String): Response<ResponseUseCategoryListDto> {
        return withContext(Dispatchers.IO) {
            UseCategoryApiInstance.useCategoryApiService.getUseCategories(userId)
        }
    }

    private fun setSpinnersAndButtonsEnable() {
        assetTypeSpinner.isEnabled = true
        dateDueSpinner.isEnabled = true
        yearMonthLeftImageButton.isEnabled = true
        yearMonthRightImageButton.isEnabled = true
        useCategorySpinner.isEnabled = true
        plusMinusSpinner.isEnabled = true
        initConditionButton.isEnabled = true
        searchLedgerButton.isEnabled = true
        addLedgerFloatingActionButton.isEnabled = true
    }

    private fun fetchGetAssetDetailTypes(userId: String, assetType: String) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetAssetDetailApiResponse(userId, assetType)
        }
    }

    private suspend fun handleGetAssetDetailApiResponse(userId : String, assetType: String) {
        try {
            val response = getAssetDetails(userId, assetType)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                useCategoryApiFlag = true
                val responseAssetDetailListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseAssetDetailDtos : List<ResponseAssetDetailDto>? =
                        responseAssetDetailListDto?.assetDetailDtos
                    val assetDetailTypes : MutableList<String> = mutableListOf("전체")

                    if (responseAssetDetailDtos != null){
                        responseAssetDetailDtos.forEach { responseAssetDetail ->
                            assetDetailTypes.add(responseAssetDetail.assetDetailType)
                        }
                    }

                    setAssetDetailTypeSpinner(assetDetailTypes)
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }

                setUseCategorySpinner(listOf("전체"))
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }

            setUseCategorySpinner(listOf("전체"))
        }
    }

    private suspend fun getAssetDetails(userId: String, assetType: String): Response<ResponseAssetDetailListDto> {
        return withContext(Dispatchers.IO) {
            AssetDetailApiInstance.assetDetailApiService.getAssetDetails(userId, assetType)
        }
    }

}