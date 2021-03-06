package ee.hm.dop.utils.security;

import ee.hm.dop.utils.DOPFileUtils;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.provider.DOMMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.security.*;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.x509.X509Credential;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.*;
import java.io.InputStream;

import static java.lang.String.format;

public class MetadataUtils {

    public static X509Credential getCredential(String credentialPath, String entityId) throws Exception {
        DocumentBuilder docBuilder = getDocumentBuilder();
        Element metadataRoot = getElement(credentialPath, docBuilder);
        MetadataCredentialResolver credentialResolver = getMetadataCredentialResolver(metadataRoot);
        CriteriaSet criteriaSet = getCriterias(entityId);
        return (X509Credential) credentialResolver.resolveSingle(criteriaSet);
    }

    private static CriteriaSet getCriterias(String entityId) {
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, SAMLConstants.SAML20P_NS));
        criteriaSet.add(new EntityIDCriteria(entityId));
        return criteriaSet;
    }

    private static MetadataCredentialResolver getMetadataCredentialResolver(Element metadataRoot) throws MetadataProviderException {
        DOMMetadataProvider idpMetadataProvider = new DOMMetadataProvider(metadataRoot);
        idpMetadataProvider.setRequireValidMetadata(true);
        idpMetadataProvider.setParserPool(new BasicParserPool());
        idpMetadataProvider.initialize();

        MetadataCredentialResolverFactory credentialResolverFactory = MetadataCredentialResolverFactory.getFactory();
        return credentialResolverFactory.getInstance(idpMetadataProvider);
    }

    private static Element getElement(String credentialPath, DocumentBuilder docBuilder) throws Exception {
        try (InputStream inputStream = DOPFileUtils.getFileAsStream(credentialPath)) {
            if (inputStream == null) {
                throw new RuntimeException(format("Failed to load credentials in path: %s", credentialPath));
            }

            Document metaDataDocument = docBuilder.parse(inputStream);
            return metaDataDocument.getDocumentElement();
        }

    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        return documentBuilderFactory.newDocumentBuilder();
    }
}
