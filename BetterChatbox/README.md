## BetterChatbox
Allows you to customize your chatbox

### Using the API

To register a custom avatar press action simply create a field named `onAvatarPress` with a View.OnClickListener type in your root plugin class

```java
public class MyPlugin extends Plugin {

public class CustomOnPress implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        Utils.showToast("Avatar Pressed", false);
    }
}

public final View.OnClickListener onAvatarPress = new CustomOnPress();
...
```

To register a custom avatar long press action simply create a field named `onAvatarLongPress` with a View.OnLongClickListener type in your root plugin class

```java
public class MyPlugin extends Plugin {

public class CustomOnLongPress implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
        Utils.showToast("Avatar Long Pressed", false);
        return true;
    }
}

public final View.OnLongClickListener onAvatarLongPress = new CustomOnLongPress();
...
```