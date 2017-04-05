### 1.2.6
#### Changes
* Added handling of primitive types as return types without boxing/unboxing in mvp_view_wrap;
* Added check if getter and setter methods are public in mvp_view_wrap;
* Imports optimization in all modules;

### 1.2.3
#### Bug fixes
* Fixed calling of super method in BaseAsyncExecutorPresenter.

### 1.2.2
#### Bug fixes
* Fixed issue for AutosavableProcessor with Parcelable handling.

### 1.2.1

#### New
* Added new modules mvp_view_wrap и mvp_view_wrap_annotation;
* Added BaseAsyncPresenter.kt to mvp_presenter.

#### Changes
* Refactored AutosavableProcessor to AutosavableProcessor2;
* BaseAsyncPresenter in mvp_presenter splited to BaseAsyncPresenter and BaseAsyncExecutorPresenter;
* BaseAsyncRxPresenter in mvp_rx_presenter renamed to BaseAsyncRxSchedulerPresenter;
* UiWaitingOnSubscribe and UiWaitingOnSubscriber moved from nested classes in BaseAsyncRxPresenter to independent classes;
* Support library version updated.

#### DemoApp changes
* Moved to native annotation processor instead of apt;
* Changes for 1.2.1 version usage.
