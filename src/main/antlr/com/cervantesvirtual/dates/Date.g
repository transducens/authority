grammar Date;

@header { 
    package com.cervantesvirtual.dates;
   
    import java.io.File;
    import java.io.FileWriter; 
    import java.net.URI;
    import java.net.URISyntaxException;
    import com.cervantesvirtual.io.Messages;
}

@lexer::header {package com.cervantesvirtual.dates;} 

@members {
         
    FileWriter writer = null;

     private void writeToLogFile (String s) {
         try {
             if (writer == null) {
                try{
                    URI uri = DateParser.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI();
                    String dir = new File(uri.getPath()).getParent();
                    File file = new File(dir, "antlr.log");
                    writer = new FileWriter(file);
                }catch (URISyntaxException ex) {
                    Messages.info(DateParser.class.getName() + ": " + ex);
                } 
             }
             writer.write(s + "\n");
             writer.flush();
         } catch (java.io.IOException e) {
             System.err.println("ERROR writing to log file");
         }
     }

     public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        writeToLogFile(hdr + " " + msg);
    }

    public void emitErrorMessage (String msg) {
        writeToLogFile(msg);
    }

    public static Period parse (String text) {
        ANTLRStringStream input = new ANTLRStringStream(text.trim());
        DateLexer lexer = new DateLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DateParser parser = new DateParser(tokens);
        Period p = null;
     
        try { 
            p = parser.date();   
        } catch (Exception x) {
            System.err.println("Warning: invalid date " + text);
        }
        if (p == null) {
            System.err.println("Warning: invalid date " + text);
        }
        return p;
    }


    public int parseYear (String s) {
        if (! s.matches("[0-9]{4}|[1-9][0-9]{0,2}")) {
            System.err.println("Warning: " + s + " is not a year number");
        }
        return Integer.parseInt(s);
    }

    /**
     *  @return numerical value of a roman number (up to XXXIX). 
     */
    public int parseRomanNumber (String roman) { 
        String s = roman.replaceAll("IV","IIII").replaceAll("IX","VIIII");
        int val = 0;
        for (Character c : s.toCharArray()) {
            if (c.equals('I')) {
                ++val;
            } else if (c.equals('V')) {
                val += 5;
            } else if (c.equals('X')) {
                val += 10;
            }
        }
        return val;
    }

    public int parseCentury (String s) {
        if (s.matches("[12][0-9]|[1-9]")) {
                return Integer.parseInt(s);
        } else if (s.matches("X?(V?(I|II|III)|I?(V|X))")) {
                return parseRomanNumber(s);
        } else {
            System.err.println("Warning: " + s + " is not a century number");
            return 0;
        }
    }
}   

// A date record
date returns [Period period] 
    : (DOT|COMMA)? (LPAR? SPACE? res=cdate SPACE? RPAR?) (DOT|COMMA)? {
         $period = $res.period;
      }
    ;


//clean date
cdate returns [Period period] 
    : year {$period = new Period($year.date, $year.date);}    
    | century {$period = new Period($century.date, $century.date);} 
    | year_range {$period = $year_range.period;} 
    | cent_range {$period = $cent_range.period;}
    ;

// unknown year or century
unknown returns [Date date] 
    : QMARK {$date = new Date(0, 0, DateType.UNKNOWN);}
    ;

//YEARS
year_num returns [Date date]  @init{int value; int range;}     
    : DIGITS {$date = new Date(parseYear($DIGITS.text), 0, DateType.YEAR);}
    | QMARK {$date = new Date(0, 0, DateType.UNKNOWN);}
    | DIGITS QMARK {   // 1927? or 195? (year 195 approx.)
            value = parseYear($DIGITS.text);
            range = Date.defaultUncertainty(value);
            $date = new Date(value, range, DateType.YEAR); 
        }
    | DIGITS QMARK QMARK {  // 16?? is interpreted as 17th century
            value = 100 * Integer.parseInt($DIGITS.text);
            range = 50;
            $date = new Date(value + range, range, DateType.YEAR);
    }
    ;


signed_year returns [Date date]
    : year_num {$date = $year_num.date;}
    | year_num SPACE+ AD {$date = $year_num.date;}
    | year_num SPACE+ BC {
            $date = $year_num.date;
            $date.setBC(); 
        }
    ;

fuzzy_year returns [Date date]
    : CIRCA SPACE* signed_year { 
            $date = $signed_year.date;
            $date.add(0, Date.defaultUncertainty($date.getValue()));
        }
    | ACTIVE SPACE* signed_year {
            $date = $signed_year.date;
            $date.add(0, 40);
        }
    ;

year returns [Date date] 
    : signed_year {$date = $signed_year.date;}  
    | fuzzy_year {$date = $fuzzy_year.date;}  
    ;

// YEAR-RANGES
year_range returns [Period period]
    : (PBORN y=year | y=year SPACE? DASH) {
            Date date = new Date($y.date);
            date.add(60, 30);
            if (date.getValue() > 2010) {  // Date is in the future
                date = new Date(0, 0, DateType.UNKNOWN);
            }
            $period = new Period($y.date, date);
        }
    | (PDEAD y=year | DASH SPACE? y=year) {
            Date date = new Date($y.date);
            date.add(-60, 30);
            $period = new Period(date, $y.date);
        }
    | y1=signed_year SPACE? DASH SPACE? y2=year {  // backwards sign inheritance
            if ($y2.date.isBC()) {
                $y1.date.setBC();
            }
            $period = new Period($y1.date, $y2.date);}
    | CIRCA SPACE* y1=signed_year SPACE? DASH SPACE? y2=year {
            // forward fuzzyness inheritance
            Date low = new Date($y1.date);
            Date high = new Date($y2.date);
            low.add(0, Date.defaultUncertainty(low.getValue()));
            high.add(0, Date.defaultUncertainty(high.getValue()));
            $period = new Period(low, high);
        }
    | ACTIVE SPACE* y1=signed_year SPACE? DASH SPACE? y2=year {  
            // forward fuzzyness inheritance
            Date low = new Date($y1.date);
            Date high = new Date($y2.date);
            low.add(-10, 10 + Date.defaultUncertainty(low.getValue()));
            high.add(10, 10 + Date.defaultUncertainty(high.getValue()));
            $period = new Period(low, high);
        }
    ;

// CENTURIES
cent_num returns [Date date] 
    : DIGITS ORD? {
            $date = new Date(parseCentury($DIGITS.text), 0, DateType.CENTURY);
        } 
    | QMARK {$date = new Date(0, 0, DateType.UNKNOWN);}
    | ROMAN {
            int val = parseCentury($ROMAN.text);
            $date = new Date(val, 0, DateType.CENTURY);
        }
    ;

signed_century returns [Date date] 
    : cent_num {$date = $cent_num.date;}
    | cent_num SPACE+ AD {$date = $cent_num.date;}
    | cent_num SPACE+ BC {
            $date = $cent_num.date;
            $date.setBC(); 
        }
    ;

// full century record 
century returns [Date date]
    : (ACTIVE SPACE?)? SEC signed_century {
            $date = $signed_century.date;
        } 
    ;

// century range 
cent_range returns [Period period]
    : century SPACE? DASH SPACE? SEC? signed_century {
          $period = new Period($century.date, $signed_century.date); 
          if ($signed_century.date.isBC()) {
            $period.setLowBC(); 
          }
      } 
    ;


// TOKENS
SPACE : ' ' ;      // Whitespace 
LPAR : '(' ;       // left parenth
RPAR : ')' ;       // right parenth
DOT : '.' ;        // The dot
DASH : '-' ;       // The dash
QMARK : '?' ;      // Question mark
DIGITS : ('0'..'9')+ ;
ROMAN : ('X'?('V'?('I'|'II'|'III')|'I'?('V'|'X'))) ; 
BC : ('BC' | 'B.C.' | ('a'|'A')('.'' '?)?('J'('.'' '?)?)?('C''.'?)) ;
AD : ('AD' | 'A.D.' | ('d'|'D')('.'' '?)?('J'('.'' '?)?)?('C''.'?)) ;
CIRCA : 'ca''.'? | 'c.' ;
ACTIVE : ('fl'|'Fl'|'F')'.'? ;
PBORN : ('b'|'n')'.'?SPACE* ;   // prefix (with space)
PDEAD : ('d'|'m')'.'?SPACE* ;   // prefix (with space)
SEC : ('S''.'?|'s''.'?|'sec''.'?|'Siglo'|'siglo')SPACE* ; // prefix (with space)
ORD : ('º'|'°'|'ð') ;
COMMA : (','|';') ;