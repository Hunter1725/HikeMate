package com.example.hikemate.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.hikemate.Converter.BitmapConverter;
import com.example.hikemate.Database.Dao.HikeDao;
import com.example.hikemate.Database.Dao.HikeImageDao;
import com.example.hikemate.Database.Dao.MessageDao;
import com.example.hikemate.Database.Dao.AnimalDao;
import com.example.hikemate.Database.Dao.ObservationDao;
import com.example.hikemate.Database.Dao.ObservationImageDao;
import com.example.hikemate.Database.Dao.PlantDao;
import com.example.hikemate.Database.Dao.SettingDao;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.HikeImage;
import com.example.hikemate.Database.Dao.WeatherDao;
import com.example.hikemate.Database.Model.Message;
import com.example.hikemate.Database.Dao.SkillDao;
import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.Database.Model.ObservationImage;
import com.example.hikemate.Database.Model.Plant;
import com.example.hikemate.Database.Model.Setting;
import com.example.hikemate.Database.Model.Weather;
import com.example.hikemate.Database.Model.Skill;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
@TypeConverters(BitmapConverter.class)



@Database(entities = {Setting.class, Weather.class, Message.class, Animal.class, Plant.class, Skill.class, Hike.class, HikeImage.class, Observation.class, ObservationImage.class}, version = 1)
public abstract class HikeDatabase extends androidx.room.RoomDatabase {
    public abstract SettingDao settingDao();

    public abstract ObservationDao observationDao();
    public abstract ObservationImageDao observationImageDao();
    public abstract HikeDao hikeDao();
    public abstract HikeImageDao hikeImageDao();
    public abstract WeatherDao weatherDao();
    public abstract MessageDao messageDao();
    public abstract AnimalDao animalDao();
    public abstract SkillDao skillDao();
    public abstract PlantDao plantDao();
    private static HikeDatabase instance;
    private static Context appContext;

    public static synchronized HikeDatabase getInstance(Context context) {
        if (instance == null){
            appContext = context.getApplicationContext();
            instance = Room.databaseBuilder(context, HikeDatabase.class, "room_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(initialCallback)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback initialCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            populateInitialData();
        }
    };

    private static void populateInitialData() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            HikeDatabase db = HikeDatabase.getInstance(appContext);
            AnimalDao animalDao = db.animalDao();
            ArrayList<Animal> animalList = new ArrayList<>();
            animalList.add(new Animal("https://wallpapercave.com/wp/wp2049736.jpg", "SƯ TỬ","LION", "10/10", "- Tránh tiếp xúc. \n- Đừng chạy hay quay lưng lại. \n- Nếu bị tấn công, hãy đối mặt với sư tử và cố tỏ ra to lớn hơn, đe dọa chúng.", "- Avoid contact. \n- Don't run or turn your back. \n- If attacked, face the lion and try to appear larger, intimidate them." ,"To lớn, khỏe mạnh, có bộ lông dày, thường có màu vàng hoặc nâu nhạt. Sư tử là thợ săn cừ khôi và có thể tấn công con người nếu cảm thấy bị đe dọa.","Large, powerful, with thick fur, usually yellow or light brown in color. Lions are excellent hunters and can attack humans if they feel threatened."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.RarCh7sn73uQFiaa7NniMAHaFj?w=269&h=202&c=7&r=0&o=5&dpr=2&pid=1.7", "HÀ MÃ", "HIPPOPOTAMUS", "9/10", "- Giữ khoảng cách an toàn. \n- Không xâm phạm không gian cá nhân của họ. \n- Tránh tiếp xúc gần và không khiêu khích họ.", "- Maintain a safe distance. \n- Don't encroach on their personal space. \n- Avoid close contact and don't provoke them.", "Lớn, chân sau cao, miệng rộng và mõm rộng. Hà mã có thể tấn công nếu chúng cảm thấy bị đe dọa hoặc đang bảo vệ con non.", "Large, with a high-set hind, wide mouth, and broad snout. Hippos can attack if they feel threatened or are protecting their young."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.sG2rW73ts64lmHsWe_HliAHaEo?w=267&h=180&c=7&r=0&o=5&dpr=2&pid=1.7","TÊ GIÁC","RHINO","8/10","- Giữ khoảng cách an toàn.\n- Tránh làm phiền chúng hoặc đến gần trẻ nhỏ.\n- Đừng bỏ chạy và tránh đối đầu.","- Keep a safe distance. \n- Avoid disturbing them or getting close to young ones. \n- Don't run and avoid confrontation.", "To lớn, có sừng dài, thân hình thon thả và đôi chân thon thả. Tê giác có thể tấn công nếu chúng cảm thấy bị đe dọa hoặc trong mùa giao phối.", "Large, with long horns, slim body, and slender legs. Rhinos can attack if they feel threatened or during mating season."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.XsNwP0PV4-6bZd7juaHaCwHaE7?w=306&h=203&c=7&r=0&o=5&dpr=2&pid=1.7", "LỢN RỪNG", "WILD BOAR", "7/10","- Giữ khoảng cách an toàn. \n- Tránh tiếp xúc gần với phụ nữ hoặc trẻ vị thành niên. \n- Tránh chạy và cố tỏ ra to lớn hơn để ngăn cản chúng.","- Keep a safe distance. \n- Avoid close contact with females or juveniles. \n- Avoid running and try to appear larger to deter them.", "Chắc chắn, đầu to, có bộ lông sẫm màu và ngà cong. Lợn rừng có thể tấn công nếu chúng cảm thấy bị đe dọa hoặc khi bảo vệ con non.","Stocky, large head, with dark fur and curved tusks. Wild boars can attack if they feel threatened or when protecting their young."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.dj8QEvRF-bluM8O-DS69KgHaE8?w=254&h=180&c=7&r=0&o=5&dpr=2&pid=1.7","KHỈ ĐUÔI DÀI","LONG-TAILED MONKEY","6/10", "- Giữ khoảng cách an toàn. \n- Đừng chạy hoặc đến gần trẻ. \n- Tránh gây căng thẳng và đừng làm chúng lo lắng.","- Keep a safe distance. \n- Don't run or approach young ones. \n- Avoid causing stress and don't make them anxious.","Thân hình nhỏ nhắn, nhanh nhẹn, đuôi dài. Khỉ đuôi dài có thể tấn công nếu cảm thấy bị đe dọa hoặc đang bảo vệ lãnh thổ của mình.", "Small, agile body, long tail. Long-tailed monkeys can attack if they feel threatened or are defending their territory."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.Jy_GsS2JNuOvMSc6RXcR-AHaE7?w=277&h=185&c=7&r=0&o=5&dpr=2&pid=1.7","CÁ SẤU","CROCODILE","9/10", "- Tránh ở gần các vùng nước. \n- Giữ khoảng cách an toàn. \n- Không bơi ở những khu vực được biết là có cá sấu hiện diện. \n- Nếu bị tấn công, hãy vùng vẫy và chuyển sự chú ý của chúng sang nơi khác.", "- Avoid close proximity to water bodies. \n- Keep a safe distance. \n- Don't swim in areas known for crocodile presence. \n- If attacked, struggle and divert their attention elsewhere.","Thân dài, khỏe, miệng rộng với hàm răng sắc nhọn. Cá sấu thường đợi con mồi ở gần vùng nước và có thể tấn công nhanh chóng.","Long body, powerful build, wide mouth with sharp teeth. Crocodiles often wait for prey near water bodies and can attack swiftly."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.0K6SYiW6cBu_E4rVxeJrnQHaE8?w=293&h=195&c=7&r=0&o=5&dpr=2&pid=1.7","RẮN ĐỘC","VENOMOUS SNAKES","9/10","- Tránh tiếp xúc. \n- Đừng chạy hoặc khiêu khích họ. \n- Nếu bị cắn, hãy bình tĩnh và tìm kiếm sự trợ giúp y tế ngay lập tức.", "- Avoid contact. \n- Don't run or provoke them. \n- If bitten, stay calm and seek medical help immediately.","Thân hình thon gọn, thường có màu sắc rực rỡ để báo hiệu nguy hiểm. Rắn độc có thể nguy hiểm nếu cảm thấy bị đe dọa","Slim body, often brightly colored to signal danger. Venomous snakes can be dangerous if they feel threatened"));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.FPnomMFebQ0Fk-aF4r8ZMQAAAA?w=282&h=180&c=7&r=0&o=5&pid=1.7", "BÁO", "LEOPARD","10/10","- Tránh tiếp xúc gần. \n- Đừng bỏ chạy hoặc quay lưng lại. \n- Nếu bị tấn công, hãy đối mặt với con báo và cố gắng đe dọa chúng.","- Avoid close contact. \n- Don't run or turn your back. \n- If attacked, face the leopard and try to intimidate them." , "To lớn, cơ bắp, có bộ lông dày, màu cam với những đốm đen. Báo hoa mai có thể tấn công nếu chúng cảm thấy bị đe dọa hoặc đói.", "Large, muscular, with a thick fur coat, orange with black spots. Leopards can attack if they feel threatened or are hungry."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.u_lqjThbn1IwgOGWd80obgHaE8?w=267&h=180&c=7&r=0&o=5&dpr=2&pid=1.7", "SÓI","WOLF","8/10","- Tránh tiếp xúc gần. \n- Đừng bỏ chạy hoặc quay lưng lại. \n- Nếu bị tấn công, hãy đối mặt với con sói và cố gắng tỏ ra to lớn hơn.","- Avoid close contact. \n- Don't run or turn your back. \n- If attacked, face the wolf and try to appear larger.","Thân hình thon gọn, bộ lông bóng mượt, màu xám hoặc nâu. Sói có thể tấn công nếu chúng cảm thấy bị đe dọa hoặc đói.", "Slim body, sleek fur, gray or brown in color. Wolves can attack if they feel threatened or are hungry."));
            animalList.add(new Animal("https://th.bing.com/th/id/OIP.kkALb9poTd5yMyihPuwmxwHaEb?w=279&h=180&c=7&r=0&o=5&dpr=2&pid=1.7","HỔ", "TIGER","10/10","- Tránh tiếp xúc. \n- Đừng bỏ chạy hoặc quay lưng lại. \n- Nếu bị tấn công, hãy đối mặt với con hổ và cố gắng đe dọa chúng.","- Avoid contact. \n- Don't run or turn your back. \n- If attacked, face the tiger and try to intimidate them.","To lớn, mạnh mẽ, có bộ lông dày, màu cam với sọc đen. Hổ là thợ săn xuất sắc và có thể tấn công con người nếu cảm thấy bị đe dọa hoặc đói.", "Large, powerful, with a thick fur coat, orange with black stripes. Tigers are excellent hunters and can attack humans if they feel threatened or are hungry."));
            animalDao.insertAll(animalList);

            PlantDao plantDao = db.plantDao();
            ArrayList<Plant> plantList = new ArrayList<>();
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.d55L1liXnK3pqoVju7hlUQHaE8?w=266&h=180&c=7&r=0&o=5&dpr=2&pid=1.7","CÂY THƯỜNG XUÂN ĐỘC","POISON IVY", "9/10","- Tránh tiếp xúc. \n- Nếu chạm vào, hãy rửa ngay vùng bị ảnh hưởng bằng xà phòng và nước.", "- Avoid contact. \n- If touched, wash the affected area immediately with soap and water.","Cây bụi nhỏ có ba lá sáng bóng, màu đỏ vào mùa xuân, màu xanh lá cây vào mùa hè và màu đỏ hoặc cam vào mùa thu. Cây thường xuân độc có chứa urushiol, gây kích ứng da hoặc phản ứng dị ứng.","Small shrub with three shiny leaves, red in spring, green in summer, and red or orange in fall. Poison Ivy contains urushiol, causing skin irritation or an allergic reaction."));
            plantList.add(new Plant("https://th.bing.com/th/id/R.d19f1315f0d245660cee9814e72bdade?rik=QEtykkgcEbKYuw&pid=ImgRaw&r=0", "CÂY SỒI ĐỌC", "POISON OAK", "8/10", "- Tránh tiếp xúc. \n- Rửa kỹ vùng bị ảnh hưởng nếu tiếp xúc.","- Avoid contact. \n- Wash affected area thoroughly if exposed.", "Tương tự như cây thường xuân độc, có ba lá, nhưng mọc dưới dạng cây bụi hoặc dây leo. Chứa urushiol, gây kích ứng da hoặc phản ứng dị ứng.", "Similar to poison ivy, with three leaves, but grows as a shrub or climbing vine. Contains urushiol, causing skin irritation or allergic reaction."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.9uWhbJJfPT1Ifk0pF-Kj-wHaE4?w=295&h=195&c=7&r=0&o=5&dpr=2&pid=1.7", "CÂY THÙ DU ĐỌC", "POISON SUMAC", "9/10", "- Tránh tiếp xúc. \n- Rửa sạch vùng đó bằng nước ngay lập tức nếu chạm vào.", "- Avoid contact. \n- Rinse the area with water immediately if touched.", "Cây bụi cao hoặc cây nhỏ có hàng lá chét cặp đôi, thường ở vùng đầm lầy. Chứa urushiol, gây kích ứng da nghiêm trọng.", "Tall shrub or small tree with rows of paired leaflets, usually in swampy areas. Contains urushiol, causing severe skin irritation."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.iR9JEeccQn7jdRvF5T1nawHaEI?w=330&h=184&c=7&r=0&o=5&dpr=2&pid=1.7", "CÂY TẦM MA", "STINGING NETTLE", "7/10", "- Đeo găng tay và mặc áo dài tay. \n- Tránh chạm vào vùng da trần. \n- Nếu bị đốt, hãy rửa vùng bị ảnh hưởng và chườm lạnh.", "- Wear gloves and long sleeves. \n- Avoid touching bare skin. \n- If stung, wash the affected area and apply a cold compress.", "Cây thân thảo có lá răng cưa, phủ đầy lông nhỏ, gây đau nhức. Gây kích ứng da và cảm giác nóng rát khi chạm vào.", "Herbaceous plant with serrated leaves covered in tiny, stinging hairs. Causes skin irritation and a burning sensation when touched."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.SA7ukGvd76lPaPCuQAf88QHaFj?w=264&h=198&c=7&r=0&o=5&dpr=2&pid=1.7", "CÂY HÀ THỦ Ô KHỔNG LỒ", "GIANT HOGWEED", "10/10", "- Tránh tiếp xúc bằng mọi giá. \n- Nếu bị tiếp xúc, hãy rửa sạch vùng đó ngay lập tức và tránh ánh nắng trực tiếp.", "- Avoid contact at all costs. \n- If exposed, wash the area immediately and keep it out of direct sunlight.", "Cây lớn có chùm hoa màu trắng, đốm tím, thân rỗng. Chứa nhựa độc, gây bỏng nặng và phồng rộp khi tiếp xúc với ánh sáng mặt trời.", "Large plant with clusters of white flowers, purple blotches, and hollow stems. Contains toxic sap causing severe burns and blisters when exposed to sunlight."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.O0QbCrK4fJNHovYV5KK68gHaD-?w=338&h=182&c=7&r=0&o=5&dpr=2&pid=1.7", "CÂY MANCHINEEL", "MANCHINEEL TREE", "10/10", "- Avoid touching or standing under the tree, especially during rain. \n- If exposed, rinse thoroughly and seek medical help.", "- Avoid touching or standing under the tree, especially during rain. \n- If exposed, rinse thoroughly and seek medical help.", "Cây nhỏ có lá xanh bóng và quả nhỏ màu xanh giống như táo. Tất cả các bộ phận của cây, bao gồm cả nhựa của nó, đều có độc tính cao.", "Small tree with glossy green leaves and small green fruits resembling apples. All parts of the tree, including its sap, are highly toxic."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.sUJsmOcWT-n95Wwmp10QVQHaE7?pid=ImgDet&rs=1", "CÀ ĐỘC DƯỢC", "JIMSONWEED", "8/10", "- Tránh chạm vào và đặc biệt là ăn bất kỳ bộ phận nào của cây. \n- Giữ khoảng cách an toàn.", "- Avoid touching and especially ingesting any part of the plant. \n- Keep a safe distance.", "Cây cao, lá to, màu xanh đậm, quả có gai và hoa hình loa kèn. Chứa alkaloid độc hại gây ảo giác và các triệu chứng nghiêm trọng khác nếu nuốt phải.", "Tall plant with large, dark green leaves, thorny fruits, and trumpet-shaped flowers. Contains toxic alkaloids causing hallucinations and other severe symptoms if ingested."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.ZLJ7Jp2K7EsIR-CRxn32pQHaE6?pid=ImgDet&rs=1", "CÂY ĐẬU CASTOR", "CASTOR BEAN PLANT", "9/10", "- Tránh tiếp xúc. \n- Không xử lý hạt giống. \n- Nếu nuốt phải, hãy tìm kiếm sự chăm sóc y tế ngay lập tức.", "- Avoid contact. \n- Do not handle the seeds. \n- If ingested, seek immediate medical attention.", "Cây lớn có lá hình cọ và quả có gai chứa hạt có độc tính cao. Chứa ricin, một chất độc chết người.", "Large plant with palmate leaves and spiky fruits containing highly toxic seeds. Contains ricin, a deadly toxin."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.MfR_i0YnAj8QFEEt76geGwHaE6?w=288&h=191&c=7&r=0&o=5&dpr=2&pid=1.7", "CÂY TRÚC ĐÀO", "OLEANDER", "9/10", "- Tránh tiếp xúc. \n- Không đốt cây trúc đào, vì khói có thể độc hại. \n- Tìm kiếm sự trợ giúp y tế nếu nuốt phải.", "- Avoid contact. \n- Do not burn Oleander, as the smoke can be toxic. \n- Seek medical help if ingested.", "Cây bụi hoặc cây nhỏ có lá màu xanh đậm và hoa thơm, nhiều màu sắc. Chứa các hợp chất độc hại ảnh hưởng đến tim và các cơ quan khác.", "Shrub or small tree with dark green leaves and colorful, fragrant flowers. Contains toxic compounds affecting the heart and other organs."));
            plantList.add(new Plant("https://th.bing.com/th/id/OIP.8t8NoH2bKbir1OWHRH4dSAHaEK?w=279&h=180&c=7&r=0&o=5&dpr=2&pid=1.7", "CÂY HUYẾT DỤ", "HEMLOCK", "10/10", "- Tránh mọi tiếp xúc. \n- Không chạm vào hoặc ăn bất kỳ bộ phận nào của cây. \n- Tìm kiếm sự chăm sóc y tế ngay lập tức nếu tiếp xúc.", "- Avoid all contact. \n- Do not touch or ingest any part of the plant. \n- Seek immediate medical attention if exposed.", "Cây cao với những chiếc lá được chia đều và những chùm hoa nhỏ màu trắng, hình chiếc ô. Chứa coniine, một chất độc chết người ảnh hưởng đến hệ thần kinh.", "Tall plant with finely divided leaves and white, umbrella-shaped clusters of small flowers. Contains coniine, a deadly poison affecting the nervous system."));
            plantDao.insertAll(plantList);

            SkillDao skillDao = db.skillDao();
            ArrayList<Skill> skillList = new ArrayList<>();
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.RGiANAwDxhxj7Hkyn9oNOQHaEK?w=264&h=180&c=7&r=0&o=5&dpr=2&pid=1.7","LẠC TRONG RỪNG SÂU","Getting Lost in Deep Forest","7/10","- Hãy bình tĩnh. \n- Cố gắng tìm các điểm đánh dấu đường mòn hoặc các địa danh tự nhiên (sông, suối) để dẫn đường. \n- Nếu không thành công, hãy thử quay lại các bước đi của bạn và khám phá các tuyến đường thay thế.","- Stay calm. \n- Try to find trail markers or natural landmarks (rivers, streams) to guide your way. \n- If unsuccessful, attempt to retrace your steps and explore alternative routes.", "Lạc sâu trong rừng, mất phương hướng, không thấy dấu vết.", "Getting lost deep in the forest, losing direction, and having no visible trail markers."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.oY6WQkPR53U5CSfxx-tOkQHaE8?w=210&h=180&c=7&r=0&o=5&dpr=2&pid=1.7", "THỜI TIẾT KHẮC NGHIỆT", "Severe Weather Conditions", "7/10", "- Kiểm tra dự báo thời tiết trước khi ra ngoài. \n- Mang theo thiết bị và quần áo phù hợp với các điều kiện thời tiết khác nhau. \n- Tìm nơi trú ẩn và đợi thời tiết cải thiện nếu gặp bão lớn.", "- Check weather forecasts before heading out. \n- Carry proper gear and clothing for various weather conditions. \n- Seek shelter and wait for the weather to improve if caught in a severe storm.", "Thời tiết thay đổi bất lợi đột ngột như mưa lớn, bão hoặc tuyết rơi.", "Sudden adverse weather changes, such as heavy rain, storms, or snowfall."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.OeIIlSLDFhLhHKwc6ZtupwHaFG?w=268&h=184&c=7&r=0&o=5&dpr=2&pid=1.7", "CHẠM TRÁN ĐỘNG VẬT HOANG DÃ", "Encounter with Dangerous Wildlife", "9/10", "- Gây tiếng ồn để cảnh báo động vật hoang dã về sự hiện diện của bạn. \n- Tránh di chuyển đột ngột và từ từ lùi lại. \n- Đừng quay lưng lại. Nếu bị tấn công, hãy bảo vệ các cơ quan quan trọng của bạn, giả chết với gấu và đánh trả báo sư tử hoặc lợn rừng.", "- Make noise to alert wildlife of your presence. \n- Avoid sudden movements, and slowly back away. \n- Do not turn your back. If attacked, protect your vital organs, play dead for bears, and fight back for cougars or boars.", "Gặp phải những động vật hung dữ như gấu, báo sư tử hoặc lợn rừng.", "Encountering aggressive animals like bears, cougars, or wild boars."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.idxXwN3Y0BpEuhKGVaqEdgHaE8?w=279&h=186&c=7&r=0&o=5&dpr=2&pid=1.7", "RƠI TỪ VÁCH ĐÁ CAO", "Falling Off Cliff or Steep Terrain", "9/10", "- Hãy chú ý bước đi của bạn. \n- Đi trên những con đường mòn đã được đánh dấu và sử dụng gậy đi bộ đường dài để giữ thăng bằng. \n- Nếu bạn trượt chân, hãy cố bám vào vật gì đó ổn định. Tránh đi đường tắt nguy hiểm.", "- Watch your step. \n- Stay on marked trails, and use hiking poles for balance. \n- If you slip, try to grab onto something stable. Avoid risky shortcuts.", "", "Slipping and falling off a cliff or steep slope."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.tV_n_wjP0E0I2VTLHZbm-wHaE8?w=256&h=180&c=7&r=0&o=5&dpr=2&pid=1.7", "MẤT NƯỚC, SAY NẮNG", "Dehydration and Heatstroke", "8/10", "- Uống nước thường xuyên. \n- Mặc quần áo nhẹ và nghỉ ngơi ở nơi có bóng râm. \n- Nhận biết các dấu hiệu say nắng (chóng mặt, mạch nhanh, lú lẫn) và tìm kiếm sự trợ giúp y tế ngay lập tức nếu các triệu chứng xảy ra.", "- Drink water regularly. \n- Wear light clothing and take breaks in shaded areas. \n- Recognize signs of heatstroke (dizziness, rapid pulse, confusion) and seek immediate medical help if symptoms occur.", "Tiếp xúc kéo dài với thời tiết nắng nóng dẫn đến mất nước và các bệnh liên quan đến nhiệt.", "Prolonged exposure to hot weather leading to dehydration and heat-related illnesses."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.IE6bJYIr2Id2SAJ_O5ODgAHaEc?pid=ImgDet&rs=1", "VƯỢT SÔNG, THÁC", "River or Water Crossing", "7/10", "- Đánh giá độ sâu và dòng chảy trước khi băng qua. \n- Sử dụng một cây gậy chắc chắn để giữ thăng bằng. \n- Tháo dây đeo ba lô phòng trường hợp bạn cần tháo nó ra nhanh chóng. \n- Băng qua ở những điểm rộng hơn, nông hơn.", "- Assess the depth and current before crossing. \n- Use a sturdy stick for balance. \n- Unbuckle backpack straps in case you need to remove it quickly. \n- Cross at wider, shallower points.", "Băng qua sông hoặc vùng nước chảy xiết.", "Crossing fast-flowing rivers or water bodies."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.jnL0MaipFeO-WaznQccRLgHaE8?w=274&h=183&c=7&r=0&o=5&dpr=2&pid=1.7", "CHẤN THƯƠNG, BONG GÂN", "Injury or Sprain", "6/10", "- Mang giày dép phù hợp. \n- Sử dụng gậy leo núi để giữ thăng bằng và thận trọng khi đi trên địa hình không bằng phẳng. \n- Mang theo bộ sơ cứu cơ bản. \n- Nghỉ ngơi và điều trị vết thương nhẹ kịp thời.", "- Wear proper footwear. \n- Use trekking poles for balance, and be cautious on uneven terrain. \n- Carry a basic first aid kit. \n- Rest and treat minor injuries promptly.", "Chấn thương do tai nạn hoặc bong gân khi đi bộ đường dài.", "Accidental injuries or sprains while hiking."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.--h74a-6m9yZAbn98yK7xQHaEK?w=306&h=180&c=7&r=0&o=5&dpr=2&pid=1.7", "CÔN TRÙNG TẤN CÔNG", "Attack by Insects or Bees", "7/10", "- Mặc quần áo bảo hộ. \n- Sử dụng thuốc chống côn trùng, tránh quần áo sáng màu và các sản phẩm có mùi thơm. \n- Nếu bị tấn công, hãy nhanh chóng chạy khỏi khu vực đó và tìm nơi trú ẩn trong nhà.", "- Wear protective clothing. \n- Use insect repellent, and avoid brightly colored clothing and sweet-scented products. \n- If attacked, run away from the area quickly and seek shelter indoors.", "Bị tấn công bởi đàn côn trùng, ong hoặc ong bắp cày.", "Getting attacked by swarms of insects, bees, or wasps."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.CPRWW13dnR_ayBpaJh6WJgHaD8?pid=ImgDet&rs=1", "KHÔNG CÓ NƠI TRÚ ẨN KHI TRỜI TỐI", "Nightfall without Shelter", "8/10", "- Lên kế hoạch đi bộ đường dài để đảm bảo bạn quay trở lại trước khi mặt trời lặn. \n- Mang theo đèn pin hoặc đèn pha, pin dự phòng và còi. \n- Nếu màn đêm buông xuống, hãy ở yên tại chỗ, tiết kiệm năng lượng và chờ cứu hộ nếu cần.", "- Plan your hike to ensure you return before sunset. \n- Carry a flashlight or headlamp, extra batteries, and a whistle. \n- If darkness falls, stay put, conserve energy, and wait for rescue if needed.", "Khi trời dần tối và bạn không có nơi trú ẩn thích hợp.", "Getting caught outdoors after dark without adequate shelter."));
            skillList.add(new Skill("https://th.bing.com/th/id/OIP.vxDvW-18WiEeqC2oswIX7gHaD4?w=269&h=180&c=7&r=0&o=5&dpr=2&pid=1.7", "MẮC KẸT Ở NƠI CÓ NHIỆT ĐỘ THẤP", "Getting Trapped in Severe Cold", "9/10", "- Mặc nhiều lớp. \n- Mặc quần áo giữ nhiệt và mang theo các vật dụng khẩn cấp như chăn và thiết bị đánh lửa. \n- Xây một nơi trú ẩn nếu có thể. \n- Giữ nhiệt cơ thể bằng cách đứng yên và rúc sát vào người khác nếu có. ", "- Dress in layers. \n- Wear thermal clothing and carry emergency supplies like blankets and fire-starting equipment. \n- Build a shelter if possible. \n- Conserve body heat by staying still and huddling close to others if available.", "Bị mắc kẹt trong thời tiết cực lạnh mà không có quần áo hoặc nơi trú ẩn thích hợp.", "Becoming trapped in extreme cold weather without proper clothing or shelter."));
            skillDao.insertAll(skillList);
        });
    }
}
