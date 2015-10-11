/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package abhi.medline;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 */
public class MedlineXmlDeserializer implements Iterator<Object> {

    private Object nextCitation = null;
    private final ObjectInputStream in;

    public MedlineXmlDeserializer(InputStream medlineXmlFileStream) throws FileNotFoundException, IOException {
        XStream xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(MedlineCitationSet.class);
        xstream.processAnnotations(MedlineCitation.class);

        in = xstream.createObjectInputStream(new BufferedInputStream(medlineXmlFileStream));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (nextCitation == null) {
            try {
                Object citation = in.readObject();
                nextCitation = citation;
            } catch (EOFException e) {
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Object citationToReturn = nextCitation;
        nextCitation = null;
        return citationToReturn;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
    }

}
