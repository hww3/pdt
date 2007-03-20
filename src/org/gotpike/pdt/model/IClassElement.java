package org.gotpike.pdt.model;

/**
 * Interface implemented by source elements which belong to a Package. 
 */
public interface IClassElement extends ISourceElement
{
    /**
     * @return the parent package
     */
    public Class getParent(); 
}