package com.plmph.vm;

public class AlphaInstructionOpcodes {

    public static final int NOP   = 0x00;                                   // nop()

    // External Functions
    public static final int CALLEARG = 0x01;                                // callearg
    public static final int OUT      = 0x02;                                // out
    public static final int OUTR     = 0x03;                                // outr
    public static final int PAR      = 0x04;                                // par

    // Parameter Stack
    public static final int PSB      = 0x05;                                // psb
    public static final int PSE      = 0x06;                                // pse
    public static final int CALLE    = 0x07;                                // calle
    public static final int EX       = 0x08;                                // ex
    public static final int EXRET    = 0x09;                                // exret
    public static final int EXPAR    = 0x0A;                                // expar
    public static final int PAREX    = 0x0B;                                // parex
    public static final int OUTEX    = 0x0C;                                // outex
    public static final int ESB      = 0x0D;                                // esb
    public static final int ESC      = 0x0E;                                // esc
    public static final int ESE      = 0x0F;                                // ese
    public static final int VAR      = 0x10;                                // var
    public static final int GET      = 0x11;                                // get
    public static final int SET      = 0x12;                                // set
    public static final int SETEX    = 0x13;                                // setex
    public static final int VSB      = 0x14;                                // vsb
    public static final int VSE      = 0x15;                                // vse
    public static final int ISB      = 0x16;                                // isb
    public static final int ISE      = 0x17;                                // ise
    public static final int TSB      = 0x18;                                // tsb
    public static final int TSE      = 0x19;                                // tse
    public static final int LEAVE    = 0x1A;                                // leave
    public static final int LEAVEIF  = 0x1B;                                // leaveif
    public static final int REPEAT   = 0x1C;                                // repeat
    public static final int REPEATIF = 0x1D;                                // repeatif
    public static final int IF       = 0x1E;                                // if
    public static final int IFE      = 0x1F;                                // ife

    // Variable Stack
    public static final int VARIABLE_GET = 0x30;                            // vget("varName;)         var("varName;)
    public static final int VARIABLE_SET_LITERAL = 0x31;                    // vset("varName; "val;)   var("varName; "val;)
    public static final int VARIABLE_SET_EXPRESSION_STACK_TOP = 0x32;       // vset()
    public static final int VARIABLE_STACK_SCOPE_BEGIN = 0x33;              // vssb()   ( => essb() + vssb()
    public static final int VARIABLE_STACK_SCOPE_END = 0x34;                // vsse()   ) => esse() + vsse()

    // Instruction Scope Stack
    public static final int INSTRUCTION_SCOPE_BEGIN = 0x40;                 // issb()   { => issb() + essb() + vssb()
    public static final int INSTRUCTION_SCOPE_END = 0x41;                   // isse()   } => isse() + esse() + vsse()

    // Comparisons
    public static final int COMPARE_EQUAL = 0x50;                           // ceq()    ?=()
    public static final int COMPARE_NOT_EQUAL = 0x51;                       // cneq()   ?!=()
    public static final int COMPARE_LESS_THAN = 0x52;                       // clt()
    public static final int COMPARE_GREATER_THAN = 0x53;                    // cgt()

    // Jumps and Branches and Loops
    public static final int SKIP_SCOPE_IF_TRUE = 0x60;                      // ssit()   ?1()
    public static final int SKIP_SCOPE_IF_FALSE = 0x61;                     // ssif()   ?0()
    public static final int LEAVE_SCOPE = 0x62;                             // ls()
    public static final int LEAVE_SCOPE_IF_TRUE = 0x63;                     // lsit()
    public static final int LEAVE_SCOPE_IF_FALSE = 0x64;                    // lsif()
    public static final int REPEAT_SCOPE = 0x65;                            // rs()
    public static final int REPEAT_SCOPE_IF_TRUE = 0x66;                    // rsit()
    public static final int REPEAT_SCOPE_IF_FALSE = 0x67;                   // rsif()
    public static final int JUMP = 0x68;                                    // jump()   =>
    public static final int JUMP_IF_TRUE = 0x69;                            // jit()    =>1
    public static final int JUMP_IF_FALSE = 0x6A;                           // jif()    =>0
    public static final int JUMP_TABLE = 0x6B;                              // jt()     =>[]

    // Internal Functions
    public static final int DEFINE_INTERNAL_FUNCTION = 0x70;                // dif()
    public static final int CALL_INTERNAL_FUNCTION = 0x71;                  // cif()    &myFunc;

    // Objects
    public static final int CREATE_OBJECT = 0x80;                           // co()
    public static final int GET_OBJECT_FIELD = 0x81;                        // gof("fieldName;)
    public static final int SET_OBJECT_FIELD_LITERAL = 0x82;                // sof("fieldName; "val;)
    public static final int SET_OBJECT_FIELD_EXPRESSION_STACK_TOP = 0x83;   // sof("fieldName;)
    public static final int DELETE_OBJECT_FIELD = 0x84;                     // dof("fieldName;)
    public static final int ADD_OBJECT_METHOD = 0x85;                       // aom("methodName; +length; )
    public static final int DELETE_OBJECT_METHOD = 0x86;                    // dom("methodName;)
    public static final int CALL_OBJECT_METHOD = 0x87;                      // com("methodName;)
    public static final int IMPLEMENT_TRAIT = 0x88;                         // it("traitName;)

    // Traits
    public static final int DEFINE_TRAIT = 0x90;                            // dt("traitName;)
    public static final int ADD_METHOD_TO_TRAIT = 0x91;                     // amtt("methodName; +length; )
    public static final int REMOVE_METHOD_FROM_TRAIT = 0x92;                // rmft("methodName;)

    // Lists
    public static final int CREATE_LIST = 0xA0;                             // cl()
    public static final int LIST_GET_ELEMENT = 0xA1;                        // lge()
    public static final int LIST_SET_ELEMENT = 0xA2;                        // lse()

    // Maps
    public static final int CREATE_MAP = 0xB0;                              // cm()
    public static final int MAP_GET_ELEMENT = 0xB1;                         // mge()
    public static final int MAP_PUT_ELEMENT = 0xB2;                         // mpe()

    // Scoped Access
    public static final int ACCESS_SCOPE_PARENT = 0xC0;                     // asp()
    public static final int ACCESS_SCOPE_ANCESTOR = 0xC1;                   // asa()


}
