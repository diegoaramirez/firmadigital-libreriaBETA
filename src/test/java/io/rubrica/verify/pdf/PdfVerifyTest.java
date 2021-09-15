/*
 * Copyright (C) 2021 
 * Authors: Misael Fernández
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.*
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.rubrica.verify.pdf;

import io.rubrica.certificate.to.Documento;
import io.rubrica.exceptions.InvalidFormatException;
import io.rubrica.exceptions.SignatureVerificationException;

import java.io.File;
import io.rubrica.utils.Utils;
import java.io.IOException;
import static org.junit.Assert.fail;
import org.junit.Test;

public class PdfVerifyTest {

    private static final String FILE01 = "/home/mfernandez/Test/Verify/01.jpg";
    private static final String FILE02 = "/home/mfernandez/Test/Verify/02.pdf";
    private static final String FILE03 = "/home/mfernandez/Test/Verify/03.pdf";
    private static final String FILE04 = "/home/mfernandez/Test/Verify/04.pdf";
    private static final String FILE05 = "/home/mfernandez/Test/Verify/05.pdf";
    private static final String FILE06 = "/home/mfernandez/Test/Verify/06.pdf";
    private static final String FILE07 = "/home/mfernandez/Test/Verify/07.pdf";
    private static final String FILE08 = "/home/mfernandez/Test/Verify/08.pdf";
    private static final String FILE09 = "/home/mfernandez/Test/Verify/09.pdf";
    private static final String FILE10 = "/home/mfernandez/Test/Verify/10.pdf";
    private static final String FILE11 = "/home/mfernandez/Test/Verify/11.pdf";
    private static final String FILE12 = "/home/mfernandez/Test/Verify/12.pdf";
    private static final String FILE13 = "/home/mfernandez/Test/Verify/13.pdf";
    private static final String FILE14 = "/home/mfernandez/Test/Verify/14.pdf";
    private static final String FILE15 = "/home/mfernandez/Test/Verify/15.pdf";
    private static final String FILE16 = "/home/mfernandez/Test/Verify/16.pdf";
    private static final String FILE17 = "/home/mfernandez/Test/Verify/17.pdf";
    private static final String FILE18 = "/home/mfernandez/Test/Verify/18.pdf";
    private static final String FILE19 = "/home/mfernandez/Test/Verify/19.pdf";
    private static final String FILE20 = "/home/mfernandez/Test/Verify/20.pdf";
    private static final String FILE21 = "/home/mfernandez/Test/Verify/21.pdf";
    private static final String FILE22 = "/home/mfernandez/Test/Verify/22.pdf";
    private static final String FILE23 = "/home/mfernandez/Test/Verify/23.pdf";
    private static final String FILE24 = "/home/mfernandez/Test/Verify/24.pdf.p7m";
    private static final String FILE25 = "/home/mfernandez/Test/Verify/25.xml";

    @Test
    public void verifyPdf() throws Exception {
        testVerifyPdf01();
        testVerifyPdf02();
        testVerifyPdf03();
        testVerifyPdf04();
        testVerifyPdf05();
        testVerifyPdf06();
        testVerifyPdf07();
        testVerifyPdf08();
        testVerifyPdf09();
        testVerifyPdf10();//50 firmas
        testVerifyPdf11();
        testVerifyPdf12();
        testVerifyPdf13();
        testVerifyPdf14();
        testVerifyPdf15();
        testVerifyPdf16();
        testVerifyPdf17();
        testVerifyPdf18();
        testVerifyPdf19();
        testVerifyPdf20();
        testVerifyPdf21();
        testVerifyPdf22();
        testVerifyPdf23();
        testVerifyP7m24();
        testVerifyXml25();
    }
    
    /*¿Es archivo PDF?
    NO
    ¿Tiene firma electrónica?
    N/A
    ¿Es de entidad autorizada?
    N/A
    ¿Tiene sello de tiempo? (característi-ca opcional)
    N/A
    ¿Es un certificado íntegro?
    N/A
    ¿Es un certificado vigente?
    N/A
    ¿Es la firma íntegra?
    N/A
    ¿El uso está autorizado para firma electrónica?
    N/A
    ¿Tiene firma vigente? (al momento de firmar el documento)
    N/A
    ¿El documento es íntegro?
    N/A
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf01() throws Exception {
        try {
            System.out.println("Archivo de imagen JPG");
            Documento documento = verificarDocumento(FILE01);
            if (documento.getError() != null) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    NO
    ¿Tiene firma electrónica?
    N/A
    ¿Es de entidad autorizada?
    N/A
    ¿Tiene sello de tiempo? (característi-ca opcional)
    N/A
    ¿Es un certificado íntegro?
    N/A
    ¿Es un certificado vigente?
    N/A
    ¿Es la firma íntegra?
    N/A
    ¿El uso está autorizado para firma electrónica?
    N/A
    ¿Tiene firma vigente? (al momento de firmar el documento)
    N/A
    ¿El documento es íntegro?
    N/A
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf02() throws Exception {
        try {
            System.out.println("Documento que no es PDF pero tiene la extensión .pdf");
            Documento documento = verificarDocumento(FILE02);
            if (documento.getError() != null) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    NO
    ¿Es de entidad autorizada?
    N/A
    ¿Tiene sello de tiempo? (característi-ca opcional)
    N/A
    ¿Es un certificado íntegro?
    N/A
    ¿Es un certificado vigente?
    N/A
    ¿Es la firma íntegra?
    N/A
    ¿El uso está autorizado para firma electrónica?
    N/A
    ¿Tiene firma vigente? (al momento de firmar el documento)
    N/A
    ¿El documento es íntegro?
    N/A
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf03() throws Exception {
        try {
            System.out.println("Documento PDF sin firma electrónica");
            Documento documento = verificarDocumento(FILE03);
            if (documento.getError() != null) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf04() throws Exception {
        try {
            System.out.println("PDF con una firma electrónica vigente de la entidad certificadora Consejo de la Judicatura");
            Documento documento = verificarDocumento(FILE04);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf05() throws Exception {
        try {
            System.out.println("PDF con una firma electrónica vigente de la entidad certificadora ANF AC");
            Documento documento = verificarDocumento(FILE05);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf06() throws Exception {
        try {
            System.out.println("PDF con una firma electrónica vigente de la entidad certificadora Banco Central del Ecuador");
            Documento documento = verificarDocumento(FILE06);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf07() throws Exception {
        try {
            System.out.println("PDF con una firma electrónica vigente de la entidad certificadora Security Data");
            Documento documento = verificarDocumento(FILE07);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    NO
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf08() throws Exception {
        try {
            System.out.println("PDF con una firma electrónica no vigente de una entidad certificadora extranjera no autorizada en el Ecuador");
            Documento documento = verificarDocumento(FILE08);
            if (documento.getDocValidate() == true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf09() throws Exception {
        try {
            System.out.println("PDF con tres firmas, una vigente y dos no vigentes, de la misma entidad certificadora");
            Documento documento = verificarDocumento(FILE09);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf10() throws Exception {
        try {
            System.out.println("PDF que contiene 50 firmas electrónicas vigentes de diversas entidades certificadoras autorizadas");
            Documento documento = verificarDocumento(FILE10);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf11() throws Exception {
        try {
            System.out.println("PDF con una firma no vigente de una entidad certificadora autorizada");
            Documento documento = verificarDocumento(FILE11);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {
                }.getClass().getEnclosingMethod().getName());
            }
            System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    NO
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf12() throws Exception {
        try {
            System.out.println("PDF con una firma vigente de una entidad certificadora autorizada y modificado posterior a la firma");
            Documento documento = verificarDocumento(FILE12);
            if (documento.getDocValidate() != true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    SI
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf13() throws Exception {
        try {
            System.out.println("PDF que contiene una firma revocada firmado durante su vigencia de la entidad certificadora autorizada (Consejo de la Judicatura)");
            Documento documento = verificarDocumento(FILE13);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    SI
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyPdf14() throws Exception {
        try {
            System.out.println("PDF que contiene una firma no vigente y una firma vigente con sellado de tiempo, de la misma  entidad certificadora autorizada (Consejo de la Judicatura)");
            Documento documento = verificarDocumento(FILE14);
            if (documento.getDocValidate() == true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI/CERTIFICADO FUNCIONARIO PUBLICO CJ NO RECONOCIDO
    ¿Tiene sello de tiempo? (característi-ca opcional)
    SI
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI/NO
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf15() throws Exception {
        try {
            System.out.println("PDF que contiene sellado de tiempo vigente y además una firma no vigente de la misma entidad certificadora");
            Documento documento = verificarDocumento(FILE15);
            if (documento.getDocValidate() == true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    SI
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    NO
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf16() throws Exception {
        try {
            System.out.println("PDF que contiene solamente el sellado de tiempo vigente de una entidad certificadora y modificado posteriormente a dicho sellado");
            Documento documento = verificarDocumento(FILE16);
            if (documento.getDocValidate() != true && documento.getSignValidate() == true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    NO/SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO/SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf17() throws Exception {
        try {
            System.out.println("PDF con dos firmas, una no vigente de una entidad no autorizada y otra vigente de una entidad autorizada");
            Documento documento = verificarDocumento(FILE17);
            if (documento.getDocValidate() == true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    NO/SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO/SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf18() throws Exception {
        try {
            System.out.println("PDF con una firma electrónica no vigente de una entidad certificadora extranjera no autorizada en el Ecuador y una firma de una entidad certificadora autorizada");
            Documento documento = verificarDocumento(FILE18);
            if (documento.getDocValidate() == true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    NO/SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO/SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    NO
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf19() throws Exception {
        try {
            System.out.println("PDF con una firma electrónica no vigente de una entidad certificadora extranjera no autorizada en el Ecuador y una firma de una entidad certificadora autorizada");
            Documento documento = verificarDocumento(FILE19);
            if (documento.getDocValidate() != true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI/SI/NO
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    SI/NO/SI
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf20() throws Exception {
        try {
            System.out.println("PDF con una firma vigente de una entidad certificadora autorizada, una firma no vigente de una entidad certificadora autorizada y una firma no válida de entidad certificadora no autorizada");
            Documento documento = verificarDocumento(FILE20);
            if (documento.getDocValidate() == true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    SI
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    NO
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf21() throws Exception {
        try {
            System.out.println("PDF con una firma invisible no vigente de una entidad certificadora autorizada y con sello de tiempo, firmado con un certificado que se encuentra caducado (fuera del plazo de vigencia)");
            Documento documento = verificarDocumento(FILE21);
            if (documento.getDocValidate() == true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    SI
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    NO
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf22() throws Exception {
        try {
            System.out.println("PDF con una firma invisible no vigente de una entidad certificadora autorizada y con sello de tiempo, firmado con un certificado que se encuentra revocado y fuera del plazo de vigencia");
            Documento documento = verificarDocumento(FILE22);
            if (documento.getDocValidate() == true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    /*¿Es archivo PDF?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    SI
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    NO
    ¿El documento es íntegro?
    NO
    Resultado DESEADO al final de la validación
    RECHAZADO*/
    public void testVerifyPdf23() throws Exception {
        try {
            System.out.println("PDF con una firma invisible no vigente de una entidad certificadora autorizada y con sello de tiempo, firmado con un certificado que se encuentra caducado y luego archivo modificado");
            Documento documento = verificarDocumento(FILE23);
            if (documento.getDocValidate() != true && documento.getSignValidate() != true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }
    
    /*¿Es archivo P7M?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyP7m24() throws Exception {
        try {
            System.out.println("P7M con una firma electrónica vigente de la entidad certificadora BCE");
            Documento documento = verificarDocumento(FILE24);
            if (documento.getSignValidate()== true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }
    
    /*¿Es archivo P7M?
    SI
    ¿Tiene firma electrónica?
    SI
    ¿Es de entidad autorizada?
    SI
    ¿Tiene sello de tiempo? (característi-ca opcional)
    NO
    ¿Es un certificado íntegro?
    SI
    ¿Es un certificado vigente?
    NO
    ¿Es la firma íntegra?
    SI
    ¿El uso está autorizado para firma electrónica?
    SI
    ¿Tiene firma vigente? (al momento de firmar el documento)
    SI
    ¿El documento es íntegro?
    SI
    Resultado DESEADO al final de la validación
    ACEPTADO*/
    public void testVerifyXml25() throws Exception {
        try {
            System.out.println("Xml con una firma electrónica vigente de la entidad certificadora ANF");
            Documento documento = verificarDocumento(FILE25);
            if (documento.getSignValidate()== true) {
                System.out.println("Se obtuvo resultado esperado");
            } else {
                fail("Problema en la validación de la clase " + new Object() {}.getClass().getEnclosingMethod().getName());
            }
        System.out.println("*******************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problemas en el documento");
        }
    }

    private Documento verificarDocumento(String file) throws IOException, SignatureVerificationException, Exception {
        File document = new File(file);
        Documento documento = Utils.verificarDocumento(document);
        System.out.println("Documento: " + documento);
        if (documento.getCertificados() != null) {
            documento.getCertificados().forEach((certificado) -> {
                System.out.println(certificado.toString());
            });
        } else {
            throw new InvalidFormatException("Documento no soportado");
        }
        return documento;
    }
}
