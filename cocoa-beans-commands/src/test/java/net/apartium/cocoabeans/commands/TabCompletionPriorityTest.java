package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TabCompletionPriorityTest {

    @Nested
    class TabCompletionResultRecordTest {

        @Test
        void constructWithSuggestionsAndPriority() {
            TabCompletionResult result = new TabCompletionResult(Set.of("a", "b"), 5);
            assertEquals(Set.of("a", "b"), result.suggestions());
            assertEquals(5, result.priority());
        }

        @Test
        void constructWithEmptySuggestions() {
            TabCompletionResult result = new TabCompletionResult(Set.of(), 0);
            assertTrue(result.suggestions().isEmpty());
            assertEquals(0, result.priority());
        }

        @Test
        void constructWithNegativePriority() {
            TabCompletionResult result = new TabCompletionResult(Set.of("x"), -10);
            assertEquals(Set.of("x"), result.suggestions());
            assertEquals(-10, result.priority());
        }

        @Test
        void constructWithHighPriority() {
            TabCompletionResult result = new TabCompletionResult(Set.of("high"), Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, result.priority());
        }

        @Test
        void equalityAndHashCode() {
            TabCompletionResult a = new TabCompletionResult(Set.of("foo"), 10);
            TabCompletionResult b = new TabCompletionResult(Set.of("foo"), 10);
            TabCompletionResult c = new TabCompletionResult(Set.of("foo"), 20);
            TabCompletionResult d = new TabCompletionResult(Set.of("bar"), 10);

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
            assertNotEquals(a, d);
        }

        @Test
        void toStringContainsFields() {
            TabCompletionResult result = new TabCompletionResult(Set.of("test"), 42);
            String str = result.toString();
            assertTrue(str.contains("test"));
            assertTrue(str.contains("42"));
        }
    }

    @Nested
    class ArgumentParserTabCompletionResultTest {

        @Test
        void threeArgConstructor() {
            ArgumentParser.TabCompletionResult result = new ArgumentParser.TabCompletionResult(Set.of("a"), 3, 50);
            assertEquals(Set.of("a"), result.result());
            assertEquals(3, result.newIndex());
            assertEquals(50, result.priority());
        }

        @Test
        void twoArgConstructorDefaultsPriorityToZero() {
            ArgumentParser.TabCompletionResult result = new ArgumentParser.TabCompletionResult(Set.of("a"), 3);
            assertEquals(Set.of("a"), result.result());
            assertEquals(3, result.newIndex());
            assertEquals(0, result.priority());
        }

        @Test
        void emptyResult() {
            ArgumentParser.TabCompletionResult result = new ArgumentParser.TabCompletionResult(Set.of(), 0);
            assertTrue(result.result().isEmpty());
            assertEquals(0, result.newIndex());
            assertEquals(0, result.priority());
        }

        @Test
        void highPriority() {
            ArgumentParser.TabCompletionResult result = new ArgumentParser.TabCompletionResult(Set.of("x"), 1, 9999);
            assertEquals(9999, result.priority());
        }

        @Test
        void negativePriority() {
            ArgumentParser.TabCompletionResult result = new ArgumentParser.TabCompletionResult(Set.of("x"), 1, -100);
            assertEquals(-100, result.priority());
        }

        @Test
        void equalityBetweenConstructors() {
            ArgumentParser.TabCompletionResult a = new ArgumentParser.TabCompletionResult(Set.of("z"), 5, 0);
            ArgumentParser.TabCompletionResult b = new ArgumentParser.TabCompletionResult(Set.of("z"), 5);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        void inequalityOnPriority() {
            ArgumentParser.TabCompletionResult a = new ArgumentParser.TabCompletionResult(Set.of("z"), 5, 10);
            ArgumentParser.TabCompletionResult b = new ArgumentParser.TabCompletionResult(Set.of("z"), 5, 20);
            assertNotEquals(a, b);
        }
    }

    @Nested
    class DefaultKeywordPriorityTest extends CommandTestBase {

        @Test
        void defaultKeywordPriorityIs1000() {
            assertEquals(CommandManager.DEFAULT_KEYWORD_PRIORITY, 1000);
            assertEquals(1000, testCommandManager.getKeywordPriority());
        }
    }

    @Nested
    class CustomKeywordPriorityTest {

        TestSender sender;

        @BeforeEach
        void setUp() {
            sender = new TestSender();
        }

        TestCommandManagerWithPriority createManager(int keywordPriority) {
            TestCommandManagerWithPriority manager = new TestCommandManagerWithPriority(keywordPriority);
            manager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
            return manager;
        }

        @Test
        void customKeywordPriorityIsReturned() {
            assertEquals(500, createManager(500).getKeywordPriority());
        }

        @Test
        void zeroKeywordPriority() {
            assertEquals(0, createManager(0).getKeywordPriority());
        }

        @Test
        void negativeKeywordPriority() {
            assertEquals(-1, createManager(-1).getKeywordPriority());
        }
    }

    @Nested
    class PrioritySortingTest extends CommandTestBase {

        @Override
        @BeforeEach
        public void before() {
            super.before();
            testCommandManager.registerArgumentTypeHandler(
                    new PriorityParser("priority", 0, 500, Set.of("alpha", "beta", "gamma"))
            );

            testCommandManager.addCommand(new PriorityTestCommand());
        }

        @Test
        void parserSuggestionsAppearWithCorrectPriority() {
            List<String> result = evaluateTabCompletion("priority-test", new String[]{""});

            assertEquals(
                    List.of("keyword-a", "keyword-b", "alpha", "beta", "gamma"),
                    result
            );
        }

        @Test
        void keywordMatchingStillWorksWithPriority() {
            assertEquals(List.of("keyword-a", "keyword-b"), evaluateTabCompletion("priority-test", "keyword"));
        }

        @Test
        void parserPrefixMatchingWorksWithPriority() {
            assertEquals(List.of("alpha"), evaluateTabCompletion("priority-test", "a"));
        }

        @Test
        void noMatchReturnsEmpty() {
            assertEquals(List.of(), evaluateTabCompletion("priority-test", "xyz"));
        }

        @Test
        void afterKeywordShowsParserSuggestions() {
            assertEquals(List.of("alpha", "beta", "gamma"), evaluateTabCompletion("priority-test", new String[]{"keyword-a", ""}));
        }

        @Test
        void afterKeywordParserPrefixFilter() {
            assertEquals(List.of("beta"), evaluateTabCompletion("priority-test", "keyword-a b"));
        }

        List<String> evaluateTabCompletion(String label, String args) {
            return testCommandManager.handleTabComplete(sender, label, args.split("\\s+"));
        }

        List<String> evaluateTabCompletion(String label, String[] args) {
            return testCommandManager.handleTabComplete(sender, label, args);
        }
    }

    @Nested
    class HigherPriorityParserSortsFirstTest {

        TestCommandManagerWithPriority testCommandManager;
        TestSender sender;

        @BeforeEach
        void setUp() {
            testCommandManager = new TestCommandManagerWithPriority(100);
            testCommandManager.registerArgumentTypeHandler(
                    new PriorityParser("highp", 0, 500, Set.of("common", "high-only"))
            );
            testCommandManager.registerArgumentTypeHandler(
                    new PriorityParser("lowp", 0, 10, Set.of("common", "low-only"))
            );
            testCommandManager.addCommand(new HighLowPriorityCommand());
            sender = new TestSender();
        }

        @Test
        void overlappingSuggestionsUseMaxPriority() {
            assertEquals(
                    List.of("common", "high-only", "sub", "low-only"),
                    testCommandManager.handleTabComplete(sender, "hl-test", new String[]{""})
            );
        }

        @Test
        void secondLevelAlsoSortsByPriority() {
            assertEquals(
                    List.of("common", "high-only", "low-only"),
                    testCommandManager.handleTabComplete(sender, "hl-test", new String[]{"sub", ""})
            );
        }

        @Test
        void alphabeticalSortWithinSamePriority() {
            assertEquals(
                    List.of("common", "high-only", "sub", "low-only"),
                    testCommandManager.handleTabComplete(sender, "hl-test", new String[]{""})
            );
        }

        @Test
        void prefixFilteringWithPriority() {
            assertEquals(List.of("common"), testCommandManager.handleTabComplete(sender, "hl-test", "co".split("\\s+")));
        }
    }

    @Nested
    class HandleTabCompleteEdgeCasesTest extends CommandTestBase {

        @Override
        @BeforeEach
        public void before() {
            super.before();
            testCommandManager.addCommand(new CommandForTest());
        }

        @Test
        void unknownCommandReturnsEmpty() {
            assertEquals(List.of(), testCommandManager.handleTabComplete(sender, "nonexistent", new String[]{""}));
        }

        @Test
        void emptyArgsReturnsEmpty() {
            assertEquals(List.of(), testCommandManager.handleTabComplete(sender, "test", new String[0]));
        }

        @Test
        void commandNameIsCaseInsensitive() {
            assertEquals(
                    testCommandManager.handleTabComplete(sender, "test", new String[]{""}),
                    testCommandManager.handleTabComplete(sender, "TEST", new String[]{""})
            );
        }

        @Test
        void resultIsSortedByPriorityThenAlphabetically() {
            List<String> result = testCommandManager.handleTabComplete(sender, "test", new String[]{""});
            assertFalse(result.isEmpty());

            int argIdx = result.indexOf("arg");
            int dashIdx = result.indexOf("-");
            assertTrue(argIdx >= 0);
            assertTrue(dashIdx >= 0);
            assertTrue(argIdx < dashIdx);
        }
    }

    @Nested
    class KeywordPriorityIntegrationTest {

        TestSender sender;

        @BeforeEach
        void setUp() {
            sender = new TestSender();
        }

        @Test
        void keywordsHigherThanParserSuggestions() {
            TestCommandManager manager = new TestCommandManager();
            manager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
            manager.addCommand(new TestSourceCommand());

            List<String> result = manager.handleTabComplete(sender, "test-source", new String[]{""});
            assertEquals("second", result.get(0));
        }

        @Test
        void lowKeywordPriorityPutsKeywordsAfterParsers() {
            TestCommandManagerWithPriority manager = new TestCommandManagerWithPriority(0);
            manager.registerArgumentTypeHandler(
                    new PriorityParser("priority", 0, 500, Set.of("alpha", "beta"))
            );
            manager.addCommand(new PriorityTestCommand());

            assertEquals(
                    List.of("alpha", "beta", "keyword-a", "keyword-b"),
                    manager.handleTabComplete(sender, "priority-test", new String[]{""})
            );
        }

        @Test
        void equalPrioritySortsAlphabetically() {
            TestCommandManagerWithPriority manager = new TestCommandManagerWithPriority(500);
            manager.registerArgumentTypeHandler(
                    new PriorityParser("priority", 0, 500, Set.of("alpha", "beta"))
            );
            manager.addCommand(new PriorityTestCommand());

            assertEquals(
                    List.of("alpha", "beta", "keyword-a", "keyword-b"),
                    manager.handleTabComplete(sender, "priority-test", new String[]{""})
            );
        }

        @Test
        void duplicateSuggestionKeepsMaxPriority() {
            TestCommandManagerWithPriority manager = new TestCommandManagerWithPriority(100);
            manager.registerArgumentTypeHandler(
                    new PriorityParser("highp", 0, 900, Set.of("shared", "high-only"))
            );
            manager.registerArgumentTypeHandler(
                    new PriorityParser("lowp", 0, 5, Set.of("shared", "low-only"))
            );
            manager.addCommand(new HighLowPriorityCommand());

            List<String> result = manager.handleTabComplete(sender, "hl-test", new String[]{""});

            int sharedIdx = result.indexOf("shared");
            int subIdx = result.indexOf("sub");
            int lowOnlyIdx = result.indexOf("low-only");
            assertTrue(sharedIdx < subIdx);
            assertTrue(subIdx < lowOnlyIdx);
        }
    }

    @Nested
    class PriorityParserTest {

        @Test
        void tabCompletionReturnsPrioritySet() {
            PriorityParser parser = new PriorityParser(0, 42, Set.of("foo", "bar"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"f"}, 0);

            var result = parser.tabCompletion(ctx);
            assertTrue(result.isPresent());
            assertEquals(Set.of("foo"), result.get().result());
            assertEquals(1, result.get().newIndex());
            assertEquals(42, result.get().priority());
        }

        @Test
        void tabCompletionEmptyForNoMatch() {
            PriorityParser parser = new PriorityParser(0, 42, Set.of("foo", "bar"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"z"}, 0);
            assertTrue(parser.tabCompletion(ctx).isEmpty());
        }

        @Test
        void tabCompletionAllMatch() {
            PriorityParser parser = new PriorityParser(0, 10, Set.of("abc", "abd"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"ab"}, 0);

            var result = parser.tabCompletion(ctx);
            assertTrue(result.isPresent());
            assertEquals(Set.of("abc", "abd"), result.get().result());
            assertEquals(10, result.get().priority());
        }

        @Test
        void tabCompletionEmptyPrefix() {
            PriorityParser parser = new PriorityParser(0, 10, Set.of("one", "two"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{""}, 0);

            var result = parser.tabCompletion(ctx);
            assertTrue(result.isPresent());
            assertEquals(Set.of("one", "two"), result.get().result());
        }

        @Test
        void tabCompletionOutOfBounds() {
            PriorityParser parser = new PriorityParser(0, 10, Set.of("one"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"one"}, 5);
            assertTrue(parser.tabCompletion(ctx).isEmpty());
        }

        @Test
        void parseMatchesCompletion() {
            PriorityParser parser = new PriorityParser(0, 0, Set.of("foo", "bar"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"foo"}, 0);

            var result = parser.parse(ctx);
            assertTrue(result.isPresent());
            assertEquals("foo", result.get().result());
            assertEquals(1, result.get().newIndex());
        }

        @Test
        void parseNoMatch() {
            PriorityParser parser = new PriorityParser(0, 0, Set.of("foo"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"baz"}, 0);
            assertTrue(parser.parse(ctx).isEmpty());
        }

        @Test
        void tryParseMatch() {
            PriorityParser parser = new PriorityParser(0, 0, Set.of("foo"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"foo"}, 0);
            assertEquals(1, parser.tryParse(ctx).getAsInt());
        }

        @Test
        void tryParseNoMatch() {
            PriorityParser parser = new PriorityParser(0, 0, Set.of("foo"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{"baz"}, 0);
            assertTrue(parser.tryParse(ctx).isEmpty());
        }

        @Test
        void tryParseOutOfBounds() {
            PriorityParser parser = new PriorityParser(0, 0, Set.of("foo"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{}, 0);
            assertTrue(parser.tryParse(ctx).isEmpty());
        }

        @Test
        void parseOutOfBounds() {
            PriorityParser parser = new PriorityParser(0, 0, Set.of("foo"));
            CommandProcessingContext ctx = new SimpleCommandProcessingContext(new TestSender(), "cmd", new String[]{}, 0);
            assertTrue(parser.parse(ctx).isEmpty());
        }
    }

    @Nested
    class SortingOrderVerificationTest extends CommandTestBase {

        @Override
        @BeforeEach
        public void before() {
            super.before();
            testCommandManager.addCommand(new TestSourceCommand());
        }

        @Test
        void tabCompletionSortedByPriorityDescThenAlphabetically() {
            List<String> result = evaluateTabCompletion("test-source", new String[]{""});

            assertEquals("second", result.get(0));

            List<String> remainingItems = result.subList(1, result.size());
            for (int i = 0; i < remainingItems.size() - 1; i++) {
                assertTrue(remainingItems.get(i).compareTo(remainingItems.get(i + 1)) <= 0,
                        remainingItems.get(i) + " should come before " + remainingItems.get(i + 1));
            }
        }

        @Test
        void prefixFilterMaintainsSortOrder() {
            assertEquals(List.of("three", "two"), evaluateTabCompletion("test-source", "t"));
        }

        @Test
        void nestedTabCompletionSortsCorrectly() {
            assertEquals(
                    List.of("0", "1", "2", "3", "one", "three", "two", "zero"),
                    evaluateTabCompletion("test-source", new String[]{"second", "test2", ""})
            );
        }

        @Test
        void nestedPrefixFilterMaintainsSortOrder() {
            assertEquals(List.of("three", "two"), evaluateTabCompletion("test-source", "second test2 t"));
        }

        @Test
        void nestedPrefixNoMatchReturnsEmpty() {
            assertEquals(List.of(), evaluateTabCompletion("test-source", "second test2 xyz"));
        }

        List<String> evaluateTabCompletion(String label, String args) {
            return testCommandManager.handleTabComplete(sender, label, args.split("\\s+"));
        }

        List<String> evaluateTabCompletion(String label, String[] args) {
            return testCommandManager.handleTabComplete(sender, label, args);
        }
    }

    @Nested
    class ExistingBehaviorWithPriorityTest extends CommandTestBase {

        @Override
        @BeforeEach
        public void before() {
            super.before();
            testCommandManager.addCommand(new CommandForTest());
        }

        @Test
        void keywordsBeforeParserSuggestionsInTopLevel() {
            List<String> result = testCommandManager.handleTabComplete(sender, "test", new String[]{""});

            List<String> keywords = List.of("arg", "config", "diff-arg", "no", "one", "optional", "rm", "send", "set", "test", "testing", "testing-arg", "testing2", "testing3", "try", "yes");
            List<String> parserSuggestions = List.of("-", "1", "2", "3", "4", "5", "6", "7", "8", "9");

            for (String kw : keywords) {
                if (!result.contains(kw)) continue;
                for (String ps : parserSuggestions) {
                    if (!result.contains(ps)) continue;
                    assertTrue(result.indexOf(kw) < result.indexOf(ps));
                }
            }
        }

        @Test
        void keywordsAreSortedAlphabeticallyAmongThemselves() {
            List<String> result = testCommandManager.handleTabComplete(sender, "test", new String[]{""});

            List<String> keywords = List.of("arg", "config", "diff-arg", "no", "one", "optional", "rm", "send", "set", "test", "testing", "testing-arg", "testing2", "testing3", "try", "yes");
            List<String> presentKeywords = result.stream().filter(keywords::contains).toList();

            for (int i = 0; i < presentKeywords.size() - 1; i++) {
                assertTrue(presentKeywords.get(i).compareTo(presentKeywords.get(i + 1)) <= 0);
            }
        }

        @Test
        void prefixFilterKeepsSortOrder() {
            assertEquals(
                    List.of("test", "testing", "testing-arg", "testing2", "testing3"),
                    testCommandManager.handleTabComplete(sender, "test", "te".split("\\s+"))
            );
        }

        @Test
        void booleanSuggestionsAfterKeywordPath() {
            assertEquals(
                    List.of("false", "true"),
                    testCommandManager.handleTabComplete(sender, "test", new String[]{"rm", "-rf", "/*", ""})
            );
        }

        @Test
        void noSuggestionsForCompleteArgs() {
            assertEquals(List.of(), testCommandManager.handleTabComplete(sender, "test", "rm -rf /* true a".split("\\s+")));
        }
    }

    @Command("hl-test")
    static class HighLowPriorityCommand implements CommandNode {

        @SubCommand("<highp>")
        public void highCmd(Sender sender, String value) {
            sender.sendMessage("high: " + value);
        }

        @SubCommand("<lowp>")
        public void lowCmd(Sender sender, String value) {
            sender.sendMessage("low: " + value);
        }

        @SubCommand("sub <highp>")
        public void subHigh(Sender sender, String value) {
            sender.sendMessage("sub-high: " + value);
        }

        @SubCommand("sub <lowp>")
        public void subLow(Sender sender, String value) {
            sender.sendMessage("sub-low: " + value);
        }

        @Override
        public boolean fallbackHandle(Sender sender, String label, String[] args) {
            sender.sendMessage("fallback");
            return true;
        }
    }

    static class TestCommandManagerWithPriority extends CommandManager {

        TestCommandManagerWithPriority(int keywordPriority) {
            super(
                    java.util.logging.Logger.getLogger(TestCommandManagerWithPriority.class.getName()),
                    new SimpleArgumentMapper(),
                    new SimpleCommandLexer()
            );

            setKeywordPriority(keywordPriority);
        }

        @Override
        protected void addCommand(CommandNode commandNode, Command command) {
        }

        @Override
        public boolean handle(Sender sender, String commandName, String[] args) {
            try {
                return super.handle(sender, commandName, args);
            } catch (Throwable e) {
                sender.sendMessage("error");
                return false;
            }
        }
    }
}