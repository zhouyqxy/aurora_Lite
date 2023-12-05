package com.aurora.config.datasource;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Collections;


/**
 * 全局事务配置类
 */
@Aspect
@Configuration
public class TransactionConfig {
    // 切入点
    private static final String AOP_POINTCUT_EXPRESSION = "execution(* com.aurora.service..*.*(..))";
    // 需要添加事务切入的方法列表, 使用这些前缀的方法都会被添加事务
    private static final String[] REQUIRED_RULE_TRANSACTION = {"insert*", "create*", "add*", "save*", "update*", "del*", "delete*"};
    private static final String[] READ_RULE_TRANSACTION = {"select*", "get*", "query*", "search*", "count*", "find*","list*","page*"};
    @Autowired
//    private PlatformTransactionManager transactionManager;
    private TransactionManager transactionManager;

    @Bean
    public TransactionInterceptor txAdvice() {
        // 增删改事务
        RuleBasedTransactionAttribute REQUIRED = new RuleBasedTransactionAttribute();
        // 当抛出设置的对应异常后，进行事务回滚（此处设置为“Exception”级别）
        REQUIRED.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        // 设置隔离级别（读已提交的数据）
        REQUIRED.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置传播行为（存在事务则加入其中，不存在则新建事务）
        REQUIRED.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 只读事务，不做更新操作
        RuleBasedTransactionAttribute READONLY = new RuleBasedTransactionAttribute();
        // 当抛出设置的对应异常后，进行事务回滚（此处设置为“Exception”级别）
        READONLY.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        // 设置隔离级别（读已提交的数据）
        READONLY.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置传播行为（如果当前存在事务，则加入这个事务，如果当前没有事务，就以非事务方式执行）
        READONLY.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        // 设置事务是否“只读”（非必需，只是声明该事务中不会进行修改数据库的操作，可减轻由事务造成的数据库压力，属于性能优化的推荐配置）
        READONLY.setReadOnly(true);

        // 事务管理规则，承载需要进行事务管理的方法名（模糊匹配）及设置的事务管理属性
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

        // 增删改方法的前缀
        for (String s : REQUIRED_RULE_TRANSACTION) {
            source.addTransactionalMethod(s, REQUIRED);
        }
        // 查询方法的前缀
        for (String s : READ_RULE_TRANSACTION) {
            source.addTransactionalMethod(s, READONLY);
        }

        // 实例化事务拦截器
        return new TransactionInterceptor(transactionManager, source);
    }

    @Bean
    public Advisor txAdviceAdvisor() {
        // 声明切点要切入的面
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 设置需要被拦截的路径
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        // 设置切面和配置好的事务管理
        return new DefaultPointcutAdvisor(pointcut, txAdvice());
    }
}

