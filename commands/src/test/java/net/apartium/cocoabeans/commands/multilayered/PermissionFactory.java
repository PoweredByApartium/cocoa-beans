package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.TestSender;
import net.apartium.cocoabeans.commands.requirements.*;

public class PermissionFactory implements RequirementFactory {


    @Override
    public Requirement getRequirement(GenericNode node, Object obj) {
        if (obj == null)
            return null;

        if (obj instanceof Permission permission) {
            return new PermissionImpl(permission, permission.value());
        }

        return null;
    }

    private static class PermissionImpl implements Requirement {

        private final Permission permission;
        private final String value;

        public PermissionImpl(Permission permission, String value) {
            this.permission = permission;
            this.value = value;
        }

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            Sender sender = context.sender();
            if (!(sender instanceof TestSender testSender))
                return RequirementResult.error(new UnmetPermissionResponse(
                        this,
                        context,
                        "You don't have permission to execute this command"
                ));

            if (!testSender.hasPermission(value))
                return RequirementResult.error(new UnmetPermissionResponse(
                        this,
                        context,
                        "You don't have permission to execute this command"
                ));

            return RequirementResult.meet();

        }

        private class UnmetPermissionResponse extends UnmetRequirementResponse {

            public UnmetPermissionResponse(Requirement requirement, RequirementEvaluationContext context, String message) {
                super(requirement, context, message, permission);
            }

            @Override
            public Exception getError() {
                return new PermissionException(this, permission);
            }
        }
    }


}
