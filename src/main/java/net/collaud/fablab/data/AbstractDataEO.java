package net.collaud.fablab.data;

import javax.persistence.Inheritance;

/**
 *
 * @author gaetan
 */
@Inheritance
abstract public class AbstractDataEO {
	abstract public Integer getId();
}
