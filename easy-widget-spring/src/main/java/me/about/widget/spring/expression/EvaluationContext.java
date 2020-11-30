package me.about.widget.spring.expression;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/13 23:53
 * @Description:
 */
class EvaluationContext extends MethodBasedEvaluationContext {

    private final Set<String> unavailableVariables = new HashSet<String>(1);


    public EvaluationContext(Object rootObject, Method method, Object[] arguments,
                             ParameterNameDiscoverer parameterNameDiscoverer) {

        super(rootObject, method, arguments, parameterNameDiscoverer);
    }


    /**
     * Add the specified variable name as unavailable for that context.
     * Any expression trying to access this variable should lead to an exception.
     * <p>This permits the validation of expressions that could potentially a
     * variable even when such variable isn't available yet. Any expression
     * trying to use that variable should therefore fail to evaluate.
     */
    public void addUnavailableVariable(String name) {
        this.unavailableVariables.add(name);
    }


    /**
     * Load the param information only when needed.
     */
    @Override
    @Nullable
    public Object lookupVariable(String name) {
        if (this.unavailableVariables.contains(name)) {
            throw new VariableNotAvailableException(name);
        }
        return super.lookupVariable(name);
    }

    class VariableNotAvailableException extends EvaluationException {

        private final String name;


        public VariableNotAvailableException(String name) {
            super("Variable not available");
            this.name = name;
        }


        public final String getName() {
            return this.name;
        }

    }

}
