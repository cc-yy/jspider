package me.jspider.core.pipeline.filter;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.core.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * An implementation of {@link BaseItemFilter} based on SpEL.
 * Item against which the expression evaluates as literal {@code true} is dropped.
 * Reference: <a href=https://docs.spring.io/spring-framework/docs/5.2.x/spring-framework-reference/core.html#expressions>SpEL documentation</a>
 */
@Slf4j
@Component
public class SpelItemFilter extends BaseItemFilter {
    Expression expression;

    @Override
    public void open(Setting setting) {
        super.open(setting);
        if (!active) {
            return;
        }

        try {
            expression = new SpelExpressionParser().parseExpression(expr);
        } catch (ParseException e) {
            log.error("Error occurred when parsing spel: {}", expr, e);
            throw new SpiderRuntimeException("illegal spel" + expr, e);
        }

    }

    @Override
    protected boolean drop(SpiderDataItem item) {
        try {
            EvaluationContext context = new StandardEvaluationContext(item);
            return (boolean) expression.getValue(context);
        } catch (EvaluationException e) {
            log.error("Error occurred when evaluating spel: {}, item: {}", expr, item, e);
            return true;
        }
    }
}
