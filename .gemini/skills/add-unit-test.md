### Skill: /add-unit-test

**Goal:** Generate comprehensive, production-ready unit tests for targeted ViewModels or UseCases following the project's testing standards.

#### Tech Stack & Libraries
- **Test Framework:** JUnit5 (`org.junit.jupiter.api.*`)
- **Mocking:** MockK (`io.mockk.*`)
- **Flow Testing:** Turbine (`app.cash.turbine.test`)
- **Coroutines:** `kotlinx.coroutines.test` (`runTest`, `StandardTestDispatcher`)

#### Architectural & Testing Rules
1. **Naming Standard:** Use backtick descriptive method names: `` `given <condition> when <action> then <expected result>` ``.
2. **Structure:** Structure every test using the **AAA (Arrange, Act, Assert)** pattern with explicit code comments.
3. **Coroutine Rule:** Always create and swap `MainDispatcherRule` or use `UnconfinedTestDispatcher` / `StandardTestDispatcher` for Coroutine testing. Do not use `Thread.sleep()`.
4. **Flow Rule:** Test all `StateFlow` and `SharedFlow` emissions using Turbine's `.test { ... }` block.
5. **Mock Verification:** Verify interactions using `coVerify` for suspend functions and `verify` for standard calls. Use `confirmVerified()` where appropriate.

#### Template Structure to Follow

```kotlin
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class {TargetClass}Test {

    private val testDispatcher = StandardTestDispatcher()

    // Mocks
    private val dependencyMock: DependencyInterface = mockk()

    // SUT (System Under Test)
    private lateinit var viewModel: {TargetClass}

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = {TargetClass}(dependencyMock)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given success response when fetch executes then update ui state to success`() = runTest {
        // Arrange
        val expectedData = "Sample Data"
        coEvery { dependencyMock.getData() } returns Result.success(expectedData)

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(UiState.Initial, awaitItem())
            
            viewModel.loadData()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(UiState.Success(expectedData), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { dependencyMock.getData() }
    }
}