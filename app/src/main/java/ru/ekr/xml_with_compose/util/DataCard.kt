package ru.ekr.xml_with_compose.util

data class DataCard(
    val id: Int,
    val title: String = mockTitle.random(),
    val body: String = mockBody.random(),
)


fun generatedDataCard(count: Int, lastId: Int? = null): MutableList<DataCard> {
    return  MutableList(count){ DataCard(id = it + (lastId?.let { id -> id+ 1} ?: 0) ) }
}

const val GENERATED_COUNT = 3000

private val mockTitle = arrayListOf(
    "Синий стол",
    "Быстрый автомобиль",
    "Красные розы",
    "Чистый офис",
    "Смешанный напиток",
    "Старый дом",
    "Зеленый лес",
    "Большой экран",
    "Легкий ветер",
    "Темная ночь",
    "Широкий мост",
    "Простая задача",
    "Новая книга",
    "Мягкий ковер",
    "Тихий угол",
)

private val mockBody = arrayListOf(
    "Высокая гора",
    "Узкий коридор",
    "Холодный снег",
    "Густой туман",
    "Огромный берег",
    "Мокрая тропа",
    "Светлая комната",
    "Горячий кофе",
    "Острые ножи",
    "Прозрачное окно",
    "Тихий ветер",
    "Старый мост",
    "Свежий воздух",
    "Глубокий лес",
    "Темное небо",
    "Легкий дождь",
    "Жаркое солнце",
    "Стальные цепи",
    "Белый цветок",
    "Медленный поток",
)