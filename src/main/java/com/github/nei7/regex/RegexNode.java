package com.github.nei7.regex;

public abstract class RegexNode {
    public abstract <T> T accept(RegexNodeVisitor<T> visitor);

    public static abstract class BinaryNode extends RegexNode {
        protected final RegexNode left;
        protected final RegexNode right;

        protected BinaryNode(RegexNode left, RegexNode right) {
            this.left = left;
            this.right = right;
        }

        public RegexNode getLeft() {
            return left;
        }

        public RegexNode getRight() {
            return right;
        }
    }

    public static class Literal extends RegexNode {
        private final String value;

        public Literal(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public <T> T accept(RegexNodeVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Concat extends BinaryNode {
        public Concat(RegexNode left, RegexNode right) {
            super(left, right);
        }

        @Override
        public <T> T accept(RegexNodeVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Union extends BinaryNode {
        public Union(RegexNode left, RegexNode right) {
            super(left, right);
        }

        @Override
        public <T> T accept(RegexNodeVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Star extends RegexNode {
        private final RegexNode inner;

        public Star(RegexNode inner) {
            this.inner = inner;
        }

        public RegexNode getInner() {
            return inner;
        }

        @Override
        public <T> T accept(RegexNodeVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}