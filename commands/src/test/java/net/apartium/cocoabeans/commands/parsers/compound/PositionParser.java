package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.space.Position;

public class PositionParser extends CompoundParser<Position> {

    public static final String DEFAULT_KEYWORD = "position";

    public PositionParser(int priority, String keyword) {
        super(keyword, Position.class, priority, new SimpleArgumentMapper(), new SimpleCommandLexer());
    }

    public PositionParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @WithParser(DoubleParser.class)
    @ParserVariant("<double> <double> <double>")
    public Position pos(double x, double y, double z) {
        return new Position(x, y, z);
    }

    @WithParser(DoubleParser.class)
    @WithParser(value = IntParser.class, priority = 1)
    @ParserVariant("<int> <double> <int>")
    public Position pos(int x, double y, int z) {
        return new Position(x * 5, y, z);
    }

}
