#  Домашнее задание №5 

Цигельник Юля  
Б9124-09.03.03пикд(3)

---

## Тесты

| Тип | Количество |
|-----|------------|
| Юнит-тесты | 7 |
| Интеграционные тесты | 5 |
| Нетривиальные тесты | 3 |

---

## Покрытые сценарии

### Юнит-тесты 
| № | Тест | Что проверяет |
|---|------|---------------|
| 1 | `initialState_isLoadingFalseAndListEmpty` | Корректное начальное состояние экрана |
| 2 | `loadAnimeList_success_updatesUiState` | Успешная загрузка данных |
| 3 | `loadAnimeList_error_updatesErrorMessage` | Ошибка загрузки |
| 4 | `retry_afterError_loadsDataAgain` | Retry после ошибки и восстановление состояния |
| 5 | `searchAnime_emptyResult_setsEmptyState` | Корректная обработка пустого результата поиска |
| 6 | `toDomainOrNull_validData_returnsAnime` | Корректное преобразование моделей |
| 7 | `toDomainOrNull_blankTitle_returnsNull` | Обработка некорректных данных |

---

### Интеграционные тесты

#### Data layer (Room + Repository)
- `setFavourite_savesToRoom` — сохранение в избранное
- `addFavouriteTwice_noDuplicate` — отсутствие дублей
- `removeFavourite_deletesFromRoom` — удаление из избранного

#### UI layer (Compose)
- `errorState_clickRetry_transitionsToSuccess` — retry после ошибки
- `emptySearchResult_displaysEmptyMessage` — отображение empty state

---

### Нетривиальные тесты

| № | Тест | Контракт поведения |
|---|------|---------------------|
| 1 | `addFavouriteTwice_noDuplicate` | Повторное добавление в избранное не создаёт дубль |
| 2 | `retry_afterError_loadsDataAgain` | Retry после ошибки корректно восстанавливает состояние и данные |
| 3 | `errorState_clickRetry_transitionsToSuccess` | UI корректно переходит из Error в Success через retry |

---

Flow не используется