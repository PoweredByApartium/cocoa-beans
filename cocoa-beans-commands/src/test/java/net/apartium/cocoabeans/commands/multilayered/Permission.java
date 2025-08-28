package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.requirements.CommandRequirementType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CommandRequirementType(PermissionFactory.class)
public @interface Permission {

    String value();

}
