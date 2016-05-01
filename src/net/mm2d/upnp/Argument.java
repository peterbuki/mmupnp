/*
 * Copyright(C) 2016 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.upnp;

/**
 * Argumentを表現するクラス。
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介(OHMAE Ryosuke)</a>
 */
public class Argument {
    /**
     * ServiceDescriptionのパース時に使用するビルダー
     *
     * @see Device#loadDescription()
     * @see Service#loadDescription(HttpClient)
     * @see Action.Builder
     */
    public static class Builder {
        private Action mAction;
        private String mName;
        private boolean mInputDirection;
        private String mRelatedStateVariableName;
        private StateVariable mRelatedStateVariable;

        /**
         * インスタンス作成
         */
        public Builder() {
        }

        /**
         * このArgumentを保持するActionを登録する。
         *
         * @param action このArgumentを保持するAction
         */
        public void setAction(Action action) {
            mAction = action;
        }

        /**
         * Argument名を登録する。
         *
         * @param name Argument名
         */
        public void setName(String name) {
            mName = name;
        }

        /**
         * Directionの値を登録する
         *
         * "in"の場合のみinput、それ以外をoutputと判定する。
         *
         * @param direction Directionの値
         */
        public void setDirection(String direction) {
            mInputDirection = "in".equalsIgnoreCase(direction);
        }

        /**
         * RelatedStateVariableの値を登録する。
         *
         * @param name RelatedStateVariableの値
         */
        public void setRelatedStateVariableName(String name) {
            mRelatedStateVariableName = name;
        }

        /**
         * RelatedStateVariableの値を返す。
         *
         * @return RelatedStateVariableの値
         */
        public String getRelatedStateVariableName() {
            return mRelatedStateVariableName;
        }

        /**
         * RelatedStateVariableので指定されたStateVarialbeのインスタンスを登録する。
         *
         * @param variable StateVariableのインスタンス
         */
        public void setRelatedStateVariable(StateVariable variable) {
            mRelatedStateVariable = variable;
        }

        /**
         * Argumentのインスタンスを作成する。
         *
         * @return Argumentのインスタンス
         */
        public Argument build() {
            return new Argument(this);
        }
    }

    private final Action mAction;
    private final String mName;
    private final boolean mInputDirection;
    private final StateVariable mRelatedStateVariable;

    private Argument(Builder builder) {
        mAction = builder.mAction;
        mName = builder.mName;
        mInputDirection = builder.mInputDirection;
        mRelatedStateVariable = builder.mRelatedStateVariable;
    }

    /**
     * このArgumentを保持するActionを返す。
     *
     * @return このArgumentを保持するAction
     */
    public Action getAction() {
        return mAction;
    }

    /**
     * Argument名を返す。
     *
     * @return Argument名
     */
    public String getName() {
        return mName;
    }

    /**
     * Input方向か否かを返す。
     *
     * @return Inputの場合true
     */
    public boolean isInputDirection() {
        return mInputDirection;
    }

    /**
     * RelatedStateVariableでしてされたStateVariableのインスタンスを返す。
     *
     * @return StateVariableのインスタンス
     */
    public StateVariable getRelatedStateVariable() {
        return mRelatedStateVariable;
    }
}
