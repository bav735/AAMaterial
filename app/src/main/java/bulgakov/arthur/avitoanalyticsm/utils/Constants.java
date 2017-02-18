package bulgakov.arthur.avitoanalyticsm.utils;

import android.app.Fragment;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentFavouriteAds;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentMakeMark;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentPreferences;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentSavedSearches;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentSearch;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentShowAd;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentShowAds;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentShowGraph;

public class Constants {
   public static final String ET_GRAPH_RANGE_KEY = "edit_text_preference_graph_range";
   public static final String ET_GRAPH_MA_KEY = "edit_text_preference_graph_ma";
   public static final String ET_ADS_RANGE_KEY = "edit_text_preference_graph_ads_range";
   public static final String LOCATION_KEY = "list_preference_location";
   public static final String SAVED_SEARCH_KEY = "saved_search_";
   public static final String SAVED_SEARCH_SIZE_KEY = "saved_search_size";
   public static final String FAVOURITE_AD_KEY = "favourite_ad_";
   public static final String FAVOURITE_AD_SIZE_KEY = "favourite_ad_size";
   public static final String SAVED_SEARCH_ADS_KEY = "saved_search_ads_";
   public static final String SAVED_SEARCH_ADS_SIZE_KEY = "saved_search_ads_size";

   public static final String GET_ADS_OK = "OK";
   public static final String GET_ADS_EMPTY = "EXCHANGE EMPTY";
   public static final String GET_ADS_404 = "404 NOT FOUND";
   public static final String GET_ADS_REFUSED = "REFUSED";
   public static final String GET_ADS_ERROR = "ERROR";

   public static final String[] NOT_EXCHANGE_KEYWORDS = {
           "обмен не",
           "обмена не",
           "не меняю",
           "без обмен",
           "без торга и обмен",
           "никакого торга и обмен",
           "обменом не занимаюсь",
   };

   public static final String[] NOT_EXCHANGE_MODELS = {
           "iphone",
           "айфон",
           "5s",
           "5ку",
   };

   public final static String APP_TAG = "avito_analyticsm_tag";
   public final static String AVITO_URL = "https://m.avito.ru";
   public final static int CONNECTION_TIMEOUT_MSEC = 4500;
   public final static int AD_TIMEOUT_MIN = 90;
   public static final int MIN_TIME_MS_PER_SEARCH = 10000;
   public static final int TRACKING_ADS_NUM = 7;
   public static final String DEFAULT_LOCATION = "Казань";
   public static final int DEFAULT_SELLER_TYPE = 1;

   public static String[] fragmentTitles;
   public static Fragment[] fragments;
   public static String[] fragmentTags;
   public static int[] fragmentMenuItemIds;
   public static String[] categories;
   public static String[] sellerTypes;
   public static int[] treeParentNum;
   public static int[] treeHeight;
   public static String[] locations;
//   public static final String[] locationValues = {"1", "2", "3", "4"};

   public static void initialize(Context context) {
      Log.d(Constants.APP_TAG, "initialized");
      PreferenceManager.setDefaultValues(context, R.xml.fragment_preferences, true);

      locations = new String[]{"Россия",
              "Москва",
              "Санкт-Петербург",
              "Казань",
              "Набережные Челны",
      };

      treeParentNum = new int[]{-1,
              0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //все категории
              1, 1, 1, 1, 1, //транспорт
              2, 2, 2, 2, 2, 2, 2, //недвижимость
              3, 3, // работа
              4, 4, // услуги
              5, 5, 5, 5, 5, // личные вещи
              6, 6, 6, 6, 6, 6, // для дома и дачи
              7, 7, 7, 7, 7, 7, 7, 7, 7, // бытовая электроника
              8, 8, 8, 8, 8, 8, 8, // хобби и отдых
              9, 9, 9, 9, 9, 9, // животные
              10, 10, // для бизнеса
              11, 11, // автомобили
              12, 12, 12, 12, 12, 12, 12, // мотоциклы и мототехника
              13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, // грузовики и спецтехника
              14, 14, 14, 14, 14, 14, // водный транспорт
              15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, // запчасти и аксессуары
              16, 16, 16, 16, // куплю, продам, сдам, сниму недвижимость
              17, 17, 17, 17, // куплю, продам, сдам, сниму недвижимость
              18, 18, 18, 18, // куплю, продам, сдам, сниму недвижимость
              19, 19, 19, 19, // куплю, продам, сдам, сниму недвижимость
              20, 20, 20, 20, // куплю, продам, сдам, сниму недвижимость
              21, 21, 21, 21, // куплю, продам, сдам, сниму недвижимость
              22, 22, 22, 22, // куплю, продам, сдам, сниму недвижимость
              //работа и услуги убраны тк очень много пунктов
              27, 27, 27, // одежда, обувь, аксессуары
              28, 28, // детская одежда и обувь
              29, 29, 29, 29, 29, 29, 29, 29, 29, //товары для детей и игрушки
              30, 30, 30, // часы и украшения
              31, 31, 31, 31, 31, 31, // красота и здоровье
              32, 32, 32, 32, 32, // бытовая техника
              33, 33, 33, 33, 33, 33, 33, 33, 33, 33, // мебель и интерьер
              34, 34, // посуда и товары для кухни
              36, 36, 36, 36, 36, 36, 36, 36, // ремонт и строительство
              38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, // аудио и видео
              39, 39, 39, 39, // игры, приставки и программы
              42, 42, 42, 42, 42, 42, 42, // оргтехника и расходники
              43, 43, 43, // планшеты и электронные книги
              44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, // телефоны
              45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, // товары для компьютера
              46, 46, 46, 46, 46, 46, // фототехника
              47, 47, 47, 47, 47, 47, 47, // билеты и путешествия
              48, 48, 48, 48, 48, // велосипеды
              49, 49, 49, // книги и журналы
              50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, // коллекционирование
              51, 51, 51, 51, 51, 51, 51, 51,
              53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53,
              58, 58, 58, 58, 58, 58, 58, 58,
              60, 60, 60, 60, 60, 60, 60, 60, 60,
              61, 61, 61, 61, 61, 61,
      };
      categories = new String[]{"",
              "Транспорт",
              "Недвижимость",
              "Работа",
              "Услуги",
              "Личные вещи",
              "Для дома и дачи",
              "Бытовая электроника",
              "Хобби и отдых",
              "Животные",
              "Для бизнеса",

              "Автомобили",
              "Мотоциклы и мототехника",
              "Грузовики и спецтехника",
              "Водный транспорт",
              "Запчасти и аксессуары",

              "Квартиры",
              "Комнаты",
              "Дома, дачи, коттеджи",
              "Земельные участки",
              "Гаражи и машиноместа",
              "Коммерческая недвижимость",
              "Недвижимость за рубежом",

              "Вакансии",
              "Резюме",

              "Предложения услуг",
              "Запросы на услуги",

              "Одежда, обувь, аксессуары",
              "Детская одежда и обувь",
              "Товары для детей и игрушки",
              "Часы и украшения",
              "Красота и здоровье",

              "Бытовая техника",
              "Мебель и интерьер",
              "Посуда и товары для кухни",
              "Продукты питания",
              "Ремонт и строительство",
              "Растения",

              "Аудио и видео",
              "Игры, приставки и программы",
              "Настольные компьютеры",
              "Ноутбуки",
              "Оргтехника и расходники",
              "Планшеты и электронные книги",
              "Телефоны",
              "Товары для компьютера",
              "Фототехника",

              "Билеты и путешествия",
              "Велосипеды",
              "Книги и журналы",
              "Коллекционирование",
              "Музыкальные инструменты",
              "Охота и рыбалка",
              "Спорт и отдых",

              "Собаки",
              "Кошки",
              "Птицы",
              "Аквариум",
              "Другие животные",
              "Товары для животных",

              "Готовый бизнес",
              "Оборудование для бизнеса",

              "С пробегом",
              "Новый",

              "Багги",
              "Вездеходы",
              "Картинг",
              "Квадроциклы",
              "Мопеды и скутеры",
              "Мотоциклы",
              "Снегоходы",

              "Автобусы",
              "Автодома",
              "Автокраны",
              "Бульдозеры",
              "Грузовики",
              "Коммунальная техника",
              "Лёгкий транспорт",
              "Погрузчики",
              "Прицепы",
              "Сельхозтехника",
              "Строительная техника",
              "Техника для лесозаготовки",
              "Тягачи",
              "Экскаваторы",

              "Вёсельные лодки",
              "Гидроциклы",
              "Катера и яхты",
              "Каяки и каноэ",
              "Моторные лодки",
              "Надувные лодки",

              "Запчасти",
              "Аксессуары",
              "GPS-навигаторы",
              "Автокосметика и автохимия",
              "Аудио- и видеотехника",
              "Багажники и фаркопы",
              "Инструменты",
              "Прицепы",
              "Противоугонные устройства",
              "Тюнинг",
              "Шины, диски и колёса",
              "Экипировка",

              "Продам",
              "Сдам",
              "Куплю",
              "Сниму",

              "Продам",
              "Сдам",
              "Куплю",
              "Сниму",

              "Продам",
              "Сдам",
              "Куплю",
              "Сниму",

              "Продам",
              "Сдам",
              "Куплю",
              "Сниму",

              "Продам",
              "Сдам",
              "Куплю",
              "Сниму",

              "Продам",
              "Сдам",
              "Куплю",
              "Сниму",

              "Продам",
              "Сдам",
              "Куплю",
              "Сниму",

              "Женская одежда",
              "Мужская одежда",
              "Аксессуары",

              "Для девочек",
              "Для мальчиков",

              "Автомобильные кресла",
              "Велосипеды и самокаты",
              "Детская мебель",
              "Детские коляски",
              "Игрушки",
              "Постельные принадлежности",
              "Товары для кормления",
              "Товары для купания",
              "Товары для школы",

              "Бижутерия",
              "Часы",
              "Ювелирные изделия",

              "Косметика",
              "Парфюмерия",
              "Приборы и аксессуары",
              "Средства гигиены",
              "Средства для волос",
              "Средства для похудения",

              "Для дома",
              "Для индивидуального ухода",
              "Для кухни",
              "Климатическое оборудование",
              "Другое",

              "Компьютерные столы и кресла",
              "Кровати, диваны и кресла",
              "Кухонные гарнитуры",
              "Освещение",
              "Подставки и тумбы",
              "Предметы интерьера, искусство",
              "Столы и стулья",
              "Текстиль и ковры",
              "Шкафы и комоды",
              "Другое",

              "Посуда",
              "Товары для кухни",

              "Двери",
              "Инструменты",
              "Камины и обогреватели",
              "Окна и балконы",
              "Потолки",
              "Садовая техника",
              "Сантехника и сауна",
              "Стройматериалы",

              "MP3-плееры",
              "Акустика, колонки, сабвуферы",
              "Видео, DVD и Blu-ray плееры",
              "Видеокамеры",
              "Кабели и адаптеры",
              "Микрофоны",
              "Музыка и фильмы",
              "Музыкальные центры, магнитолы",
              "Наушники",
              "Телевизоры и проекторы",
              "Усилители и ресиверы",
              "Аксессуары",

              "Игры для приставок",
              "Игровые приставки",
              "Компьютерные игры",
              "Программы",

              "МФУ, копиры и сканеры",
              "Принтеры",
              "Телефония",
              "ИБП, сетевые фильтры",
              "Уничтожители бумаг",
              "Расходные материалы",
              "Канцелярия",

              "Планшеты",
              "Электронные книги",
              "Аксессуары",

              "Alcatel",
              "Blackberry",
              "Fly",
              "HTC",
              "Huawei",
              "iPhone",
              "Lenovo",
              "LG",
              "Motorola",
              "MTS",
              "Nokia",
              "Philips",
              "Samsung",
              "Siemens",
              "Sony",
              "Vertu",
              "Другие марки",
              "Аксессуары",
              "Номера и SIM-карты",
              "Рации",
              "Стационарные телефоны",

              "Акустика",
              "Веб-камеры",
              "Джойстики и рули",
              "Клавиатура и мыши",
              "Комплектующие",
              "Мониторы",
              "Переносные жёсткие диски",
              "Сетевое оборудование",
              "ТВ-тюнеры",
              "Флэшки и карты памяти",
              "Аксессуары",

              "Компактные фотоаппараты",
              "Зеркальные фотоаппараты",
              "Плёночные фотоаппараты",
              "Бинокли и телескопы",
              "Объективы",
              "Оборудование и аксессуары",

              "Карты, купоны",
              "Концерты",
              "Путешествия",
              "Спорт",
              "Театр, опера, балет",
              "Цирк, кино",
              "Шоу, мюзикл",

              "Горные",
              "Дорожные",
              "ВМХ",
              "Детские",
              "Запчасти и аксессуары",

              "Журналы",
              "Книги",
              "Учебная литература",

              "Банкноты",
              "Билеты",
              "Вещи знаменитостей, автографы",
              "Военные вещи",
              "Грампластинки",
              "Документы",
              "Жетоны, медали, значки",
              "Игры",
              "Календари",
              "Картины",
              "Киндер-сюрприз",
              "Конверты и почтовые карточки",
              "Макеты оружия",
              "Марки",
              "Модели",
              "Монеты",
              "Открытки",
              "Пепельницы, зажигалки",
              "Пластиковые карточки",
              "Спортивные карточки",
              "Фотографии, письма",
              "Этикетки, бутылки, пробки",
              "Другое",

              "Аккордеоны, гармони, баяны",
              "Гитары и другие струнные",
              "Духовые",
              "Пианино и другие клавишные",
              "Скрипки и другие смычковые",
              "Ударные",
              "Для студии и концертов",
              "Аксессуары",

              "Бильярд и боулинг",
              "Дайвинг и водный спорт",
              "Единоборства",
              "Зимние виды спорта",
              "Игры с мячом",
              "Настольные игры",
              "Пейнтбол и страйкбол",
              "Ролики и скейтбординг",
              "Теннис, бадминтон, пинг-понг",
              "Туризм",
              "Фитнес и тренажёры",
              "Другое",

              "Амфибии",
              "Грызуны",
              "Кролики",
              "Лошади",
              "Рептилии",
              "С/х животные",
              "Хорьки",
              "Другое",

              "Интернет-магазин",
              "Общественное питание",
              "Производство",
              "Развлечения",
              "Сельское хозяйство",
              "Строительство",
              "Сфера услуг",
              "Торговля",
              "Другое",

              "Для магазина",
              "Для офиса",
              "Для ресторана",
              "Для салона красоты",
              "Промышленное",
              "Другое",
      };

      sellerTypes = new String[]{"Не важно",
              "Частное лицо",
              "Компания",};

      treeHeight = new int[treeParentNum.length];
      treeHeight[0] = 0;
      for (int i = 1; i < treeParentNum.length; i++)
         treeHeight[i] = treeHeight[treeParentNum[i]] + 1;

      fragments = new Fragment[]{FragmentSavedSearches.newInstance(),
              FragmentFavouriteAds.newInstance(),
              FragmentMakeMark.newInstance(),
              FragmentPreferences.newInstance(),
              FragmentSearch.newInstance(),
              FragmentShowAds.newInstance(),
              FragmentShowGraph.newInstance(),
              FragmentShowAd.newInstance()};
      fragmentTags = new String[]{FragmentSavedSearches.FRAGMENT_TAG,
              FragmentFavouriteAds.FRAGMENT_TAG,
              FragmentMakeMark.FRAGMENT_TAG,
              FragmentPreferences.FRAGMENT_TAG,
              FragmentSearch.FRAGMENT_TAG,
              FragmentShowAds.FRAGMENT_TAG,
              FragmentShowGraph.FRAGMENT_TAG,
              FragmentShowAd.FRAGMENT_TAG,};
      int[] fragmentNamesResId = new int[]{R.string.title1,
              R.string.title2,
              R.string.title3,
              R.string.title4,
              R.string.title5,
              R.string.title6,
              R.string.title7,
              R.string.title8,};
      fragmentMenuItemIds = new int[]{R.id.nav_1,
              R.id.nav_2,
              R.id.nav_3,
              R.id.nav_4,};
      fragmentTitles = new String[fragmentNamesResId.length];
      for (int i = 0; i < fragmentTitles.length; i++) {
         fragmentTitles[i] = context.getString(fragmentNamesResId[i]);
      }
   }
}
