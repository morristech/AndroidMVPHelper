# AndroidMVPHelper

![alt tag](https://img.shields.io/badge/version-1.4.14-brightgreen.svg)

Library manages lifecycle of Activities and Fragments, their Presenters and ViewStates.

Look at [Wiki](https://github.com/Ufkoku/AndroidMVPHelper/wiki) for more details.

For library usage add:

```gradle
repositories {
    maven { url 'https://dl.bintray.com/ufkoku/maven/' }
}
```

### [mvp_base](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_base)

```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp_base:$mvp_ver"
}
```

Contains basic interfaces without any implementation.

### [mvp](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_base)

```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp:$mvp_ver"
}
```

Contains implementations of:
* [Retainable elements](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable):
  * [BaseRetainableActivity](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable/BaseRetainableActivity.kt);
  * [BaseRetainableFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable/BaseRetainableFragment.kt);
  * [BaseRetainableDialogFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable/BaseRetainableDialogFragment.kt);
* [Savable elements](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable):
  * [BaseSavableActivity](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable/BaseSavableActivity.kt);
  * [BaseSavableFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable/BaseSavableFragment.kt);
  * [BaseSavableDialogFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable/BaseSavableDialogFragment.kt).

Examples of usage:
* [BaseRetainableActivity](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/retainable)
* [BaseRetainableFragment](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/fragments/retainable)
* [BaseSavableActivity](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/savable)
* [BaseSavableFragment](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/fragments/savable)

### [mvp_presenter](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_presenter)

```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp_presenter:$mvp_ver"
}
```

Contains implementations of:
* [IPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_base/src/main/kotlin/com/ufkoku/mvp_base/presenter/IPresenter.kt) - [BasePresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BasePresenter.kt)
* [IAsyncPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_base/src/main/kotlin/com/ufkoku/mvp_base/presenter/IAsyncPresenter.kt) -  [BaseAsyncPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BaseAsyncPresenter.kt) and [BaseAsyncExecutorPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BaseAsyncExecutorPresenter.kt)

### [mvp_rx_presenter](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_rx_presenter)

```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp_rx_presenter:$mvp_ver"
}
```

Contains [BaseAsyncRxSchedulerPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_rx_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/rx/BaseAsyncRxSchedulerPresenter.kt)

### [mvp_autosavable](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_autosavable) and [mvp_autosavable_annotation](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_autosavable)

* If you are using kotlin:
```gradle
dependencies{  
  compile("com.ufkoku.mvp:mvp_autosavable_annotation:$mvp_ver")
  kapt("com.ufkoku.mvp:mvp_autosavable:$mvp_ver")
}

kapt {
  generateStubs = true
}
```
* else you should use [APT](https://bitbucket.org/hvisser/android-apt)
```gradle
buildscript {
    repositories {
      mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {    
    compile("com.ufkoku.mvp:mvp_autosavable_annotation:$mvp_ver")
    apt("com.ufkoku.mvp:mvp_autosavable:$mvp_ver")    
}
```

This modules generates classes for marked ViewStates for saving and restoring from Bunble.

### [mvp_list](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_list)

```gradle
repositories {
    maven { url 'https://dl.bintray.com/ufkoku/maven/' }
}

dependencies{
  compile "com.ufkoku.mvp:mvp_list:$mvp_ver"
}
```

Contains classes for fast implementing of infinite scrolling RecyclerView lists, with optional items such as progress bars, empty and error stub views, swipe to refresh, search.

NOTE: Use only with retainable fragments/activities!

### [mvp_view_wrap](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_view_wrap) and [mvp_view_wrap_annotation](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_view_wrap_annotation)

* If you are using kotlin:
```gradle
dependencies{  
  compile("com.ufkoku.mvp:mvp_view_wrap_annotation:$mvp_ver")
  kapt("com.ufkoku.mvp:mvp_view_wrap:$mvp_ver")
}

kapt {
  generateStubs = true
}
```
* else you should use [APT](https://bitbucket.org/hvisser/android-apt)
```gradle
buildscript {
    repositories {
      mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {    
    compile("com.ufkoku.mvp:mvp_view_wrap_annotation:$mvp_ver")
    apt("com.ufkoku.mvp:mvp_view_wrap:$mvp_ver")    
}
```

This modules generates wrap-classes for marked interfaces.

```license
Copyright 2016 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
