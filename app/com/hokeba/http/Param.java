package com.hokeba.http;

/**
 * Created by hendriksaragih on 3/19/17.
 */
public class Param {

    public final static String OPERATOR_LIKE = "like";
    public final static String OPERATOR_EQUAL = "equal";

    private String name;
    private Object value;
    private String operator;
    private String matchFirst;
    private String matchLast;
    private boolean required = false;

    public Param() {

    }

    /**
     * Construct parameters with equal operator
     *
     * @param name
     * @param value
     *
     * @author AgusZulvani
     */
    public Param(String name, Object value) {
        this.name = name;
        this.value = value;

        operator = OPERATOR_EQUAL;
    }

    /**
     * Construct parameters with equal operator
     *
     * @param name
     * @param value
     * @param required
     *
     * @author AgusZulvani
     */
    public Param(String name, Object value, boolean required) {
        this.name = name;
        this.value = value;
        this.required = required;

        operator = OPERATOR_EQUAL;
    }

    /**
     * Construct parameters with specific operator.<br/>
     * If you use Param.OPERATOR_EQUAL, the first and last match will be set to
     * % (any character)
     *
     * @param name
     * @param value
     * @param operator
     *            one of like or equal, you can use cons Param.OPERATOR_LIKE or
     *            Param.OPERATOR_EQUAL
     *
     * @author AgusZulvani
     */
    public Param(String name, Object value, String operator) {
        this.name = name;
        this.value = value;
        this.operator = (operator != null
                && (operator.equals(Param.OPERATOR_EQUAL) || (operator.equals(Param.OPERATOR_LIKE))) ? operator : null);

        if (this.operator != null && this.operator.equals(Param.OPERATOR_LIKE)) {
            matchFirst = "%";
            matchLast = "%";
        }
    }

    /**
     * Construct parameters
     *
     * @param name
     * @param value
     * @param operator
     *            one of like or equal, you can use cons Param.OPERATOR_LIKE or
     *            Param.OPERATOR_EQUAL
     * @param matchFirst
     *            if you use Param.OPERATOR_LIKE you must specify <b>first</b>
     *            words match, for example: % for any character
     * @param matchLast
     *            if you use Param.OPERATOR_LIKE you must specify <b>last</b>
     *            words match, for example: % for any character
     *
     * @author AgusZulvani
     */
    public Param(String name, Object value, String operator, String matchFirst, String matchLast) {
        this.name = name;
        this.value = value;
        this.operator = (operator != null
                && (operator.equals(Param.OPERATOR_EQUAL) || (operator.equals(Param.OPERATOR_LIKE))) ? operator : null);

        if (this.operator != null && this.operator.equals(Param.OPERATOR_LIKE)) {
            this.matchFirst = matchFirst;
            this.matchLast = matchLast;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getMatchFirst() {
        return matchFirst;
    }

    public void setMatchFirst(String matchFirst) {
        this.matchFirst = matchFirst;
    }

    public String getMatchLast() {
        return matchLast;
    }

    public void setMatchLast(String matchLast) {
        this.matchLast = matchLast;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}