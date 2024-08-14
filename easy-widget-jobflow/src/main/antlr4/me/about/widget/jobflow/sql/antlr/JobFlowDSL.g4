// 定义语法文件的名称，通常与DSL名称相关
grammar JobFlowDSL;

// 程序的起始规则，可以包含多个任务依赖表达式，用分号分隔
//taskFlow: (jobFlowId ':=')?  taskSequences* EOF;


jobFlow: jobFlowId ':=' jobBlock EOF;

jobFlowId: VARIABLE;

jobBlock: '{' taskSequences ';'* '}';

taskSequences: taskSequence (SEMICOLON taskSequence)*;

// 任务序列，支持右递归，每个任务依赖项都是前一个任务的右子节点
taskSequence: task (ARROW taskSequence)?;

// 任务可以是单个任务名或任务组
task: taskName | taskGroup;

// 任务名称，由字母或数字组成
taskName: VARIABLE;

// 任务组，用括号包围的任务列表，表示并行执行的任务
taskGroup: LPAREN (task (COMMA task)*)? RPAREN;



// 任务名称，字母或数字组合
VARIABLE: IDENTIFIER;

// 定义分隔符和操作符
COLON: ':';
ARROW: '->';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
SEMICOLON: ';';
IDENTIFIER: [a-zA-Z0-9]+; // 标识符定义


// 忽略空白字符，如空格、制表符、换行符
WS: [ \t\r\n]+ -> skip;