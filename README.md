# AndroidMVPHelper

![alt tag](https://img.shields.io/badge/version-4.0.4-brightgreen.svg)

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
  api "com.ufkoku.mvp:mvp_base:$mvp_ver"
}
```

Contains basic interfaces without any implementation.

### [mvp](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp)

```gradle
dependencies{
  api "com.ufkoku.mvp:mvp:$mvp_ver"
}
```

Contains implementations of:
* [BaseMvpActivity](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/BaseMvpActivity.kt)
* [BaseMvpDialogFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/BaseMvpDialogFragment.kt)
* [BaseMvpActivity](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/BaseMvpFragment.kt)

Examples of usage:
* [StaticListActivity](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/activity/static_list/)
* [StaticListFragment](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/fragments/static_list/)

### [mvp_presenter](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_presenter)

```gradle
dependencies{
  api "com.ufkoku.mvp:mvp_presenter:$mvp_ver"
}
```

Contains implementations of:
* [IPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_base/src/main/kotlin/com/ufkoku/mvp_base/presenter/IPresenter.kt) - [BasePresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BasePresenter.kt)
* [IAsyncPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_base/src/main/kotlin/com/ufkoku/mvp_base/presenter/IAsyncPresenter.kt) -  [BaseAsyncPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BaseAsyncPresenter.kt) and [BaseAsyncExecutorPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BaseAsyncExecutorPresenter.kt)

### [mvp_rx2_presenter](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_rx2_presenter)

```gradle
dependencies{
  
  //Add RX2 dependencies, if you haven't one
  implementation 'io.reactivex.rxjava2:rxjava:2.1.9'
  implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'

  api "com.ufkoku.mvp:mvp_rx2_presenter:$mvp_ver"
}
```

Contains [BaseAsyncRxSchedulerPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_rx2_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/rx2/BaseAsyncRxSchedulerPresenter.kt)

### [mvp_autosavable](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_autosavable) and [mvp_autosavable_annotation](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_autosavable)

* If you are using kotlin:
```gradle
dependencies{  
  implementation("com.ufkoku.mvp:mvp_autosavable_annotation:$mvp_ver")
  kapt("com.ufkoku.mvp:mvp_autosavable:$mvp_ver")
}

kapt {
  generateStubs = true
}
```
* else
```gradle
dependencies {    
    implementation("com.ufkoku.mvp:mvp_autosavable_annotation:$mvp_ver")
    annotationProcessor("com.ufkoku.mvp:mvp_autosavable:$mvp_ver")    
}
```

This modules generates classes for marked ViewStates for saving and restoring from Bunble.

### [mvp_view_wrap](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_view_wrap) and [mvp_view_wrap_annotation](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_view_wrap_annotation)

* If you are using kotlin:
```gradle
dependencies{  
  implementation("com.ufkoku.mvp:mvp_view_wrap_annotation:$mvp_ver")
  kapt("com.ufkoku.mvp:mvp_view_wrap:$mvp_ver")
}

kapt {
  generateStubs = true
}
```
* else
```gradle
dependencies {    
    implementation("com.ufkoku.mvp:mvp_view_wrap_annotation:$mvp_ver")
    annotationProcessor("com.ufkoku.mvp:mvp_view_wrap:$mvp_ver")    
}
```

This modules generates wrap-classes for marked interfaces.

```license
Copyright 2017 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)

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
