### Skill: /generate-mock-flow

**Goal:** Generate a complete Clean Architecture data flow (UiState, ViewModel, Repository interface, and Mock Repository implementation) pre-filled with realistic mock data for rapid UI prototyping.

#### Architecture Rules
1. **Unidirectional Data Flow (UDF):** Expose UI state via read-only `StateFlow<UiState>`.
2. **Layer Separation:**
    - `UiState`: Sealed interface representing `Loading`, `Success(data)`, and `Error(message)`.
    - `ViewModel`: Handles state transformations and launches Coroutines using `viewModelScope`.
    - `Repository`: Interface in the domain layer, with a `MockRepository` implementation in the data layer.
3. **Mocking Rule:** Use realistic, domain-specific mock data with artificial delays (`delay(1000)`) to simulate actual network calls.

#### Code Generation Template Structure

```kotlin
// 1. UI STATE
sealed interface {Feature}UiState {
    data object Loading : {Feature}UiState
    data class Success(val data: List<{DomainModel}>) : {Feature}UiState
    data class Error(val message: String) : {Feature}UiState
}

// 2. DOMAIN MODEL
data class {DomainModel}(
    val id: String,
    val title: String,
    val description: String
)

// 3. REPOSITORY INTERFACE
interface {Feature}Repository {
    fun get{Feature}Data(): Flow<Result<List<{DomainModel}>>>
}

// 4. MOCK REPOSITORY IMPLEMENTATION
class Mock{Feature}Repository : {Feature}Repository {
    override fun get{Feature}Data(): Flow<Result<List<{DomainModel}>>> = flow {
        // Simulate network delay
        delay(1000)
        
        val mockList = listOf(
            {DomainModel}(id = "1", title = "Mock Item 1", description = "First mock description"),
            {DomainModel}(id = "2", title = "Mock Item 2", description = "Second mock description")
        )
        emit(Result.success(mockList))
    }.catch { e ->
        emit(Result.failure(e))
    }
}

// 5. VIEWMODEL
@HiltViewModel
class {Feature}ViewModel @Inject constructor(
    private val repository: {Feature}Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<{Feature}UiState>({Feature}UiState.Loading)
    val uiState: StateFlow<{Feature}UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = {Feature}UiState.Loading
            repository.get{Feature}Data().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { data -> {Feature}UiState.Success(data) },
                    onFailure = { error -> {Feature}UiState.Error(error.localizedMessage ?: "Unexpected error") }
                )
            }
        }
    }
}