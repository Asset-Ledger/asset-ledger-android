package asset.ledger.asset_ledger_android.recyclerview

import android.os.Parcel
import android.os.Parcelable
import asset.ledger.asset_ledger_android.retrofit.ledger.dto.ResponseLedgerDto

data class LedgerRecyclerViewItem(
    // ledger 내용
    val id : Long,
    val plusMinus : String,
    val useCategory : String,
    val assetType : String,
    val assetDetailType : String,
    val amount : Int,
    val description : String,
    val createdTime : String,
    // date 내용
    val createDate : String,
    val totalPlusAmount : Int,
    val totalMinusAmount : Int,
    // 공통
    val itemType : Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(plusMinus)
        parcel.writeString(useCategory)
        parcel.writeString(assetType)
        parcel.writeString(assetDetailType)
        parcel.writeInt(amount)
        parcel.writeString(description)
        parcel.writeString(createdTime)
        parcel.writeString(createDate)
        parcel.writeInt(totalPlusAmount)
        parcel.writeInt(totalMinusAmount)
        parcel.writeInt(itemType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LedgerRecyclerViewItem> {
        override fun createFromParcel(parcel: Parcel): LedgerRecyclerViewItem {
            return LedgerRecyclerViewItem(parcel)
        }

        override fun newArray(size: Int): Array<LedgerRecyclerViewItem?> {
            return arrayOfNulls(size)
        }
    }
}
