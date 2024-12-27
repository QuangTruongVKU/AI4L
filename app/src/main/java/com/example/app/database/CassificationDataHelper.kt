package com.example.app.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CassificationDataHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1)  {
    companion object{
        private const val DATABASE_NAME = "diseases.db"

        private const val TABLE_NAME = "diseases"
        private const val ID_COLUMN = "id"
        private const val CLASS_COLUMN = "class_name"
        private const val DESCRIPTION_COLUMN = "description"
        private const val REASON_COLUMN = "reason"
        private const val SYMPTOM_COLUMN = "symptom"
        private const val DAMAGE_COLUMN = "damage"
        private const val PREVENTION_COLUMN = "prevention"
        private const val TREATMENT_COLUMN = "treatment"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        // Tạo bảng lưu class
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $ID_COLUMN INTEGER PRIMARY KEY AUTOINCREMENT,
                $CLASS_COLUMN TEXT NOT NULL,
                $DESCRIPTION_COLUMN TEXT,
                $REASON_COLUMN TEXT,
                $SYMPTOM_COLUMN TEXT,
                $DAMAGE_COLUMN TEXT,
                $PREVENTION_COLUMN TEXT,
                $TREATMENT_COLUMN TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)

        // Thêm dữ liệu mặc định cho 4 class
        val defaultClasses = listOf(
            "INSERT INTO $TABLE_NAME ($CLASS_COLUMN, $DESCRIPTION_COLUMN, $REASON_COLUMN, $SYMPTOM_COLUMN, $DAMAGE_COLUMN, $PREVENTION_COLUMN, $TREATMENT_COLUMN) " +
                    "VALUES ('Coccidiosis'," +
                    " 'Bệnh cầu trùng là một trong những bệnh phổ biến và nguy hiểm trên gia cầm," +
                    "do các loại ký sinh trùng thuộc giống Eimeria gây ra. Bệnh thường xuất hiện nhiều ở gia cầm non," +
                    "đặc biệt trong các điều kiện nuôi nhốt đông đúc hoặc môi trường vệ sinh kém.'," +
                    " '- Do các loại cầu trùng thuộc giống *Eimeria*, đặc biệt là *Eimeria tenella*.\n" +
                    "- Lây lan qua phân, thức ăn, nước uống nhiễm ký sinh trùng.\n" +
                    "- Môi trường ẩm ướt, mật độ chăn nuôi cao là yếu tố thuận lợi để bệnh bùng phát.'" +
                    ", 'Bệnh cầu trùng gà có hai dạng chính: cầu trùng manh tràng và cầu trùng ruột non, " +
                    "đôi khi cả hai thể cùng xảy ra đồng thời.\n\n" +
                    "<b>Cầu trùng manh tràng:<b>\n" +
                    "- Thường xảy ra ở gà từ 3 đến 7 tuần tuổi (khá phổ biến).\n" +
                    "- Gà có biểu hiện kêu nhiều, ăn ít, uống nước nhiều.\n" +
                    "- Gà xệ cánh, xù lông, đi phân sệt có màu đỏ nâu, phân gà sáp hoặc có máu tươi.\n\n" +
                    "<b>Cầu trùng ruột non (tá tràng):<b>\n" +
                    "- Thường gặp ở gà giò.\n" +
                    "- Gà bị viêm ruột, tiêu chảy thất thường.\n" +
                    "- Phân có lẫn máu màu nâu sậm (phân gà sáp) hoặc đôi khi thấy máu tươi.'," +
                    " '- Làm suy yếu sức khỏe của gia cầm, giảm năng suất chăn nuôi.\n" +
                    "- Tăng chi phí điều trị và phòng bệnh.\n" +
                    "- Ảnh hưởng nghiêm trọng đến chất lượng thịt và trứng.\n', " +
                    "'- Phòng bệnh bằng thuốc: Dùng thuốc trộn vào thức ăn hoặc nước uống để khống chế bệnh cầu trùng bộc phát như đã trình bày ở trên.\n" +
                    "- Sử dụng thức ăn, nước uống sạch và tránh nhiễm bẩn từ phân.\n" +
                    "- Nuôi trên nền thì phải có lớp độn chuồng hút ẩm và khô ráo, chuồng phải thông thoáng, không bị lạnh hoặc quá nóng.\n" +
                    "- Sau mỗi đợt nuôi phải làm vệ sinh và sát trùng chuồng trại với một trong các thuốc như BIO-GUARD, BIODINE, BIOXIDE hoặc BIOSEPT, sau đó thay lớp độn chuồng mới\n'," +
                    " '- Dùng các loại thuốc trị cầu trùng phổ biến như Amprolium, Sulfaquinoxaline hoặc Toltrazuril.\n" +
                    "- Dùng thuốc đặc trị cầu trùng gà (tùy theo màu phân để phân loại cầu trùng manh tràng hay cầu trùng ruột non)\n" +
                    "- Thay đệm lót chuồng, phun thuốc sát trùng 1 ngày/1 lần.\n" +
                    "- Trường hợp gà mắc cầu trùng manh tràng cần bổ sung thêm thuốc chống xuất huyết\n' )",
            "INSERT INTO $TABLE_NAME ($CLASS_COLUMN, $DESCRIPTION_COLUMN, $REASON_COLUMN, $SYMPTOM_COLUMN, $DAMAGE_COLUMN, $PREVENTION_COLUMN, $TREATMENT_COLUMN) " +
                    "VALUES ('Healthy', '', '', '', '', '', '')",
            "INSERT INTO $TABLE_NAME ($CLASS_COLUMN, $DESCRIPTION_COLUMN, $REASON_COLUMN, $SYMPTOM_COLUMN, $DAMAGE_COLUMN, $PREVENTION_COLUMN, $TREATMENT_COLUMN) " +
                    "VALUES ('New Castle Disease'," +
                    " 'Bệnh cầu trùng là một trong những bệnh phổ biến và nguy hiểm trên gia cầm," +
                    "do các loại ký sinh trùng thuộc giống Eimeria gây ra. Bệnh thường xuất hiện nhiều ở gia cầm non," +
                    "đặc biệt trong các điều kiện nuôi nhốt đông đúc hoặc môi trường vệ sinh kém.', " +
                    "'- Do các loại cầu trùng thuộc giống *Eimeria*, đặc biệt là *Eimeria tenella*.\n" +
                    "- Lây lan qua phân, thức ăn, nước uống nhiễm ký sinh trùng.\n" +
                    "- Môi trường ẩm ướt, mật độ chăn nuôi cao là yếu tố thuận lợi để bệnh bùng phát.'," +
                    " 'Kém ăn bỏ ăn, lông xù, sã cánh ỉa  chảy phân xanh, phân vàng, mào thâm.Chảy nước mắt nước mũi." +
                    "Diều càng phồng nước và thức ăn, khi dốc ngược gà xuống dưới thấy có nước chảy ra.', " +
                    "'- Làm suy yếu sức khỏe của gia cầm, giảm năng suất chăn nuôi.\n" +
                    "- Tăng chi phí điều trị và phòng bệnh.\n" +
                    "- Ảnh hưởng nghiêm trọng đến chất lượng thịt và trứng.\n', " +
                    "'- Phòng bệnh bằng thuốc: Dùng thuốc trộn vào thức ăn hoặc nước uống để khống chế bệnh cầu trùng bộc phát như đã trình bày ở trên.\n" +
                    "- Sử dụng thức ăn, nước uống sạch và tránh nhiễm bẩn từ phân.\n" +
                    "- Nuôi trên nền thì phải có lớp độn chuồng hút ẩm và khô ráo, chuồng phải thông thoáng, không bị lạnh hoặc quá nóng.\n" +
                    "- Sau mỗi đợt nuôi phải làm vệ sinh và sát trùng chuồng trại với một trong các thuốc như BIO-GUARD, BIODINE, BIOXIDE hoặc BIOSEPT, sau đó thay lớp độn chuồng mới\n', " +
                    "'- Khi cá thể gà đầu tiên có dấu hiệu mắc bệnh nhanh chóng đưa vaccin Lasota vào cho toàn đàn gà.\n" +
                    "- Sau đó tiến hành vệ sinh khử trùng chuồng trại máng ăn, máng uống, môi trường xung quanh.\n" +
                    "- Bổ sung thuốc bổ và chất điện giải nâng cao sức đề kháng cho con vật. Sử dụng kháng sinh phổ rộng tránh nhiễm trùng kế phát.\n" +
                    "- Sau khi hết liệu trình sử dụng kháng sinh thì cho con vật uống thuốc giải độc gan thận nâng cao hiệu quả chăn nuôi.\n')",
            "INSERT INTO $TABLE_NAME ($CLASS_COLUMN, $DESCRIPTION_COLUMN, $REASON_COLUMN, $SYMPTOM_COLUMN, $DAMAGE_COLUMN, $PREVENTION_COLUMN, $TREATMENT_COLUMN) " +
                    "VALUES ('Salmonella'," +
                    " '- Salmonella là một chi vi khuẩn hình que, gram âm, được công nhận là một trong những nguyên nhân phổ biến nhất gây bệnh qua thực phẩm trên toàn cầu. Salmonella là mầm bệnh nội bào khó kiểm soát bằng kháng sinh, chúng sinh sống trong đường ruột của vật chủ, bài thải qua phân và có thể làm ô nhiễm môi trường, thức ăn và nguồn nước.Các chủng Salmonella nguy hiểm\n\n" +
                    "- Chủng gây bệnh cho gia cầm: Gallinarum và Pullorum gây thương hàn gà.\n" +
                    "- Chủng gây bệnh cho người: Enteritidis, Typhimurium, Infantis, Senftenberg.\n', " +
                    "'-Do vi khuẩn Salmonella pullorum gây bệnh bạch lỵ và Salmonella gallinarum gây bệnh thương hàn.\n" +
                     "-Lây truyền qua trứng từ gà mẹ nhiễm bệnh hoặc qua tiếp xúc với phân, thức ăn, nước uống bị nhiễm vi khuẩn.\n', " +
                    "'-Kém ăn bỏ ăn, lông xù, sã cánh ỉa  chảy phân xanh, phân vàng, mào thâm.\n" +
                    "-Chảy nước mắt nước mũi.\n" +
                    "-Diều càng phồng nước và thức ăn, khi dốc ngược gà xuống dưới thấy có nước chảy ra.\n', " +
                    "'-Gây thiệt hại kinh tế do tỷ lệ chết cao ở gà con và giảm năng suất ở gà trưởng thành.\n" +
                    "-Lây lan nhanh chóng trong đàn nếu không được kiểm soát kịp thời.\n', " +
                    "'-Chọn giống từ các đàn không nhiễm bệnh.\n" +
                    "-Vệ sinh chuồng trại sạch sẽ, khử trùng định kỳ.\n" +
                    "-Kiểm soát chất lượng thức ăn, nước uống, đảm bảo không bị nhiễm khuẩn.\n" +
                    "-Áp dụng biện pháp an toàn sinh học, hạn chế người và động vật lạ vào khu vực chăn nuôi.', " +
                    "'-Sử dụng kháng sinh theo hướng dẫn của bác sĩ thú y, thường là các loại kháng sinh nhạy cảm với vi khuẩn Salmonella.\n" +
                    "-Loại bỏ những con gà nhiễm bệnh nặng để tránh lây lan.\n" +
                    "-Bổ sung vitamin và chất điện giải để tăng cường sức đề kháng cho gà.')",
        )
        defaultClasses.forEach { db?.execSQL(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    @SuppressLint("Range")
    fun getAllDiseases(): List<Disease> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val diseases = mutableListOf<Disease>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN))
            val className = cursor.getString(cursor.getColumnIndex(CLASS_COLUMN))
            val description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN))
            val reason = cursor.getString(cursor.getColumnIndex(REASON_COLUMN))
            val symptom = cursor.getString(cursor.getColumnIndex(SYMPTOM_COLUMN))
            val damage = cursor.getString(cursor.getColumnIndex(DAMAGE_COLUMN))
            val prevention = cursor.getString(cursor.getColumnIndex(PREVENTION_COLUMN))
            val treatment = cursor.getString(cursor.getColumnIndex(TREATMENT_COLUMN))

            diseases.add(Disease(id, className, description, reason, symptom, damage, prevention, treatment))
        }
        cursor.close()
        db.close()
        return diseases
    }

    data class Disease(
        val id: Int,
        val className: String,
        val description: String?,
        val reason: String?,
        val symptom: String?,
        val damage: String?,
        val prevention: String?,
        val treatment: String?
    )

}