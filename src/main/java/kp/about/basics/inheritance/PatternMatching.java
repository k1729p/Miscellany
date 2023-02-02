package kp.about.basics.inheritance;

import java.util.List;

import kp.utils.Printer;

/**
 * The pattern matching.
 *
 */
public class PatternMatching {

	/**
	 * The constructor.
	 * 
	 */
	public PatternMatching() {
		super();
	}

	/**
	 * Matching without any class cast.
	 * 
	 */
	public void withoutAnyCast() {

		final SealedInterface anonymous = new PermittedSubclassNonSealed() {
		};
		final List<SealedInterface> list = List.of(//
				new PermittedRecord(), //
				new PermittedSubclassFinal(), //
				new PermittedSubclassNonSealed(), //
				anonymous, //
				new PermittedSubclassSubclassFinal());
		list.forEach(item -> {
			String name;
			if (item instanceof PermittedRecord pr) {
				name = pr.getClass().getSimpleName();
			} else if (item instanceof PermittedSubclassFinal psf) {
				name = psf.getClass().getSimpleName();
			} else if (item instanceof PermittedSubclassNonSealed psns) {
				name = psns.getClass().getSimpleName();
				if (name.isEmpty()) {
					name = "« empty name for anonymous »";
				}
			} else if (item instanceof PermittedSubclassSubclassFinal pssf) {
				name = pssf.getClass().getSimpleName();
			} else {
				name = "unknown";
			}
			Printer.printf("simple class name[%s]", name);
		});
		Printer.printHor();
	}
}
