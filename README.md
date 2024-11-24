# Stock Exchange

## Note

I used the Polygon.io - Stock Data APIs. Almost every api I found limits the amount of requests per user which is free. You can make 5 calls per minute.

## Some Features
- Search stocks
- Sort stocks by company
- Simple animations
- Offline support
- Dark (Night) mode

## Architecture

The app is built with the Model-View-ViewModel (MVVM) is its structural design pattern that separates objects into three distinct groups:
- Models hold application data. They’re usually structs or simple classes.
- Views display visual elements and controls on the screen. They’re typically subclasses of UIView.
- View models transform model information into values that can be displayed on a view. They’re usually classes, so they can be passed around as references.

## Tech Stack

Java - Java is a popular programming language. Java is used to develop mobile apps, web apps, desktop apps, games and much more. 

### Jetpack components

- AndroidX - Major improvement to the original Android Support Library, which is no longer maintained.

- Lifecycle - Lifecycle-aware components perform actions in response to a change in the lifecycle status of another component, such as activities and fragments. These components help you produce better-organized, and often lighter-weight code, that is easier to maintain.

- ViewModel -The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way.

- LiveData - LiveData is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware, meaning it respects the lifecycle of other app components, such as activities, fragments, or services. This awareness ensures LiveData only updates app component observers that are in an active lifecycle state.

- Room database - The Room persistence library provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite. 

**Retrofit** - Retrofit is a REST client for Java/ Kotlin and Android by Square inc under Apache 2.0 license. Its a simple network library that is used for network transactions. By using this library we can seamlessly capture JSON response from web service/web API.

**GSON** - Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object.

**Lottie** - JSON-based file format used for high-quality animations.

**Dagger Hilt** - A dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project.

### Splash Screen

<img src="https://github.com/EliYakubov7/Stock-Exchange/blob/main/screenshots/splash_screen.jpg" width="250">

### Home Screen

<img src="https://github.com/EliYakubov7/Stock-Exchange/blob/main/screenshots/home_screen.jpg" width="250">

### Details Screen

<img src="https://github.com/EliYakubov7/Stock-Exchange/blob/main/screenshots/details_screen.jpg" width="250">

### Search Companies Stocks

<img src="https://github.com/EliYakubov7/Stock-Exchange/blob/main/screenshots/search_companies_stocks.jpg" width="250">

### Dropdown Companies Stocks

<img src="https://github.com/EliYakubov7/Stock-Exchange/blob/main/screenshots/dropdown_companies_stocks.jpg" width="250">

### Sort Companies Stocks

<img src="https://github.com/EliYakubov7/Stock-Exchange/blob/main/screenshots/sort_companies_stocks.jpg" width="250">

## Author

* **Eliyahu Yakubov** - *Initial work* - [Github](https://github.com/EliYakubov7), [Linkedin](https://www.linkedin.com/in/eli-yakubov-961908173)
