package ctech.firsttask.aspect;

import ctech.firsttask.model.entities.User;
import ctech.firsttask.services.context.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AspectLogger {
    private final UserContextService userContextService;
    private static final String CONTROLLERS_POINTCUT = "within(ctech.firsttask.controllers..*) && !@annotation(org.springframework.web.bind.annotation.GetMapping)";

    @Around(CONTROLLERS_POINTCUT)
    @SneakyThrows
    public Object userActionLogger(ProceedingJoinPoint pjp) {
        User user = userContextService.getCurrentUser();
        if (user != null) {
            log.info("{}, вызов : {}", user.getUsername(), constructLogMsg(pjp));
            var proceed = pjp.proceed();
            log.info("{}, конец : {}", user.getUsername(), constructLogMsg(pjp));
            return proceed;
        }else{
            log.info("{} {}, вызов : {}", "GUEST", "guest", constructLogMsg(pjp));
            var proceed = pjp.proceed();
            log.info("{} {}, конец : {}", "GUEST", "guest", constructLogMsg(pjp));
            return proceed;
        }
    }


    private String constructLogMsg(JoinPoint joinPoint) {
        var args = Arrays.stream(joinPoint.getArgs())
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
        return "@" + joinPoint.getSignature().getDeclaringTypeName() +
                "." +
                joinPoint.getSignature().getName() +
                ":" +
                args;
    }
}
