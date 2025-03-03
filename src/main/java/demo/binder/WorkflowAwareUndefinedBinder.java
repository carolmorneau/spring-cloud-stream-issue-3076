package demo.binder;

import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.BindingCreatedEvent;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.DefaultBinding;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.support.context.NamedComponent;
import org.springframework.messaging.MessageChannel;

/**
 * Workflow-aware dummy binder that fails to create any of its producers/consumers for enabled workflows.
 */
public class WorkflowAwareUndefinedBinder
		implements Binder<MessageChannel, ConsumerProperties, ProducerProperties> {
	private final ApplicationEventPublisher applicationEventPublisher;

	public WorkflowAwareUndefinedBinder(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public Binding<MessageChannel> bindConsumer(String name,
												String group,
												MessageChannel inboundBindTarget,
												ConsumerProperties consumerProperties) {
		DefaultBinding<MessageChannel> binding = new DefaultBinding<>(name, inboundBindTarget, null) {
			@Override
			public boolean isInput() {
				return true;
			}
		};

		applicationEventPublisher.publishEvent(new BindingCreatedEvent(binding));
		return binding;
	}

	String getBeanName(MessageChannel inboundBindTarget) {
		return ((NamedComponent) inboundBindTarget).getBeanName();
	}

	@Override
	public Binding<MessageChannel> bindProducer(String name,
												MessageChannel outboundBindTarget,
												ProducerProperties producerProperties) {
		DefaultBinding<MessageChannel> binding = new DefaultBinding<>(name, outboundBindTarget, null) {
			@Override
			public boolean isInput() {
				return false;
			}
		};
		applicationEventPublisher.publishEvent(new BindingCreatedEvent(binding));
		return binding;
	}

	@Override
	public String getBinderIdentity() {
		return "undefined-" + Binder.super.getBinderIdentity();
	}
}
