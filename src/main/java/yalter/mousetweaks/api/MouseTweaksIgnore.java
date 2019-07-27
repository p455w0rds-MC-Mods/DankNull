package yalter.mousetweaks.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.TYPE
})
public @interface MouseTweaksIgnore {
}
