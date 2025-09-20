Includes dataGrid that can be used in Kotlin Compose MultiPlatform : [composeApp/src/commonMain/kotlin/org/example/ktor/ComposeDataGrid.kt](https://github.com/unchil/full-stack-task-manager/blob/main/composeApp/src/commonMain/kotlin/org/example/ktor/ComposeDataGrid.kt)
* Multi Column Sorted
  - ascending, descending
* Column Data Filtering
  - Contains, Equals, Begins With, Ends With, Blank, Null
* Column Divider draggable
* Change the order of columns
* Added pagination feature
* Request parameters
   - columnNames:List<String>
   - data:List<List<Any?>>
* Accepted data types:<Any?>
  - String, Int, Long, Float, Double
* Screen Shot
![Alt text](https://github.com/unchil/full-stack-task-manager/blob/main/composeApp/src/commonMain/composeResources/composeMultiPlatform_dataGrid.png)

Original Source :[https://github.com/ktorio/ktor-documentation/tree/3.0.2/codeSnippets/snippets/tutorial-full-stack-task-manager][sorce]

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop, Server.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/server` is for the Ktor server application.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…


[sorce]: https://github.com/ktorio/ktor-documentation/tree/3.0.2/codeSnippets/snippets/tutorial-full-stack-task-manager "tutorial-full-stack-task-manager"
