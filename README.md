# PM4-Team-Toxic


The game is located under the `frontier` folder.

## Running Frontier

To run on desktop:

```sh
./gradlew lwjgl3:run
```


```bat
gradlew.bat lwjgl3:run
```

## Formatting Code

To format the code run the following task:
```sh
./gradlew spotlessApply
```


## ✅ Decision Matrix for testing frontier game

| **Criteria**                       | **Unit Tests**                              | **Integration Tests**                              | **Screenshot Tests**                               |
|-----------------------------------|---------------------------------------------|----------------------------------------------------|----------------------------------------------------|
| **Purpose**                       | Test isolated logic (e.g., damage calc)     | Test how modules work together (e.g., UI + logic)  | Test visual correctness of UI/game screens         |
| **Reliability**                   | ✅ Very high                                | ✅ High (some flakiness possible)                  | ❌ Often flaky (rendering differences, timing)     |
| **Ease of CI Integration**        | ✅ Easy (fast, low-dependency)              | ✅ Medium (may need mock setups)                   | ❌ Hard (requires rendering setup, image diffs)    |
| **Test Speed**                    | ⚡ Very fast                                 | ⚡ Medium                                           | 🐢 Slow (render, capture, compare images)          |
| **Maintenance Effort**           | ✅ Low                                      | ✅ Medium                                           | ❌ High (small UI changes cause false negatives)   |
| **Debugging Failures**            | ✅ Easy (clear logic errors)                | ⚠ Can be tricky (need context)                     | ❌ Hard (is it layout? rendering? timing?)         |
| **Tools/Framework Support**      | ✅ Wide support (JUnit, NUnit, etc.)        | ✅ Good support (TestFX, Espresso, etc.)           | ⚠ Limited and brittle (e.g., Percy, Playwright)   |
| **Catch UI/UX bugs**             | ❌ No                                       | ⚠ Some (interaction-level bugs)                    | ✅ Yes (layout/overlap issues)                     |
| **Best Use Case**                | Core logic, algorithms, model classes       | Systems like UI controller + input + sound         | Visual regression (only if critical UI stability) |
| **CI Resource Consumption**      | ✅ Low                                       | ⚠ Medium                                           | ❌ High (screenshots, image comparison tools)      |


### 🔍 Category of What You're Testing

| **What You’re Testing**         | Category                        | Recommended Approach                                 |
|-------------------------------|----------------------------------|-------------------------------------------------------|
| `ECS` Systems (Ashley)         | Unit / Integration Test         | Unit test system logic; integration test system flows |
| `Stage` (Scene2D UI logic)     | Integration Test                | Simulate clicks/inputs, mock the spritebatch if needed|
| `SpriteBatch` rendering        | Screenshot / Rendering Test     | Mock it or test visually only (not CI-friendly)       |
| **Other LibGDX Elements** (`TextureRegion`, `BitmapFont`, `ShaderProgram`, etc.) | Unit / Screnshot/Rendering Test | Assert configurations (unit); rendering only if essential | ⚠ Depends on element |

### 🧠 Focused Decision Matrix: Ashley ECS System Testing

| **ECS Component/Aspect**       | **Test Type**            | **Recommended Approach**                             | **CI Friendly** |
|-------------------------------|--------------------------|------------------------------------------------------|------------------|
| Component logic (pure data)   | ✅ Unit Test              | Directly test component state updates                | ✅ Yes            |
| System logic (processEntity)  | ✅ Unit or Integration    | Unit test with mock entities; integration with engine| ✅ Yes            |
| Engine + multiple systems     | ✅ Integration Test       | Run a full update cycle and check system interactions| ✅ Yes            |
| Entity creation/lifecycle     | ✅ Integration Test       | Create entities, add/remove components, validate flow| ✅ Yes            |
| System + Stage interaction    | ⚠ Mixed Integration Test | Simulate updates and check stage response            | ⚠ Partially      |
| ECS + rendering coordination  | ❌ Screenshot Test (only visual) | Only test if visual correctness is essential  | ❌ Hard            |
