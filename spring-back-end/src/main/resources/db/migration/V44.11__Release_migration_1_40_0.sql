SET foreign_key_checks = 0;

call insert_translation('LICENSE_TERMS_LINK', 'https://creativecommons.org/licenses/by-sa/3.0/ee/legalcode', 'https://creativecommons.org/licenses/by-sa/3.0/ee/legalcode');
call insert_translation('I_AGREE_TO', 'Olen nõus', 'I agree to the terms of');
call insert_translation('TERMS_OF_LICENSE', 'litsentsi tingimustega. nõus', 'license.');
call update_translations('MATERIAL_LICENSE_WARNING', 'Kuna materjalil puudub CC BY-SA 3.0 litsents või sellega kokkusobiv litsents (CC BY 3.0), ei saa materjali avalikuks muuta. E-koolikott ühtlustab keskkonnas kasutatavaid litsentse, ühtlustatud litsentsiks on CC BY-SA 3.0. Seega on nüüdsest võimalik materjali avalikuks muuta vaid juhul, kui nõustud nimetatud litsentsiga (litsents ei kehti lingitud sisule, mis asub keskkonnast väljas). Selleks ava allolevast nupust materjali muutmise vaade ja anna nõusolek litsentsile.',
    'Since this material is missing the CC BY-SA 3.0 or the CC BY 3.0 license, you cannot make it public. E-koolikott is consolidating the licenses being used on the website. The new license will be CC BY-SA 3.0. From this point onward, you can only make your material public, if you agree with the aforementioned license (the license does not apply to external links). To do that open the material editor and add the new license to your material.',
    'Kuna materjalil puudub CC BY-SA 3.0 litsents või sellega kokkusobiv litsents (CC BY 3.0), ei saa materjali avalikuks muuta. E-koolikott ühtlustab keskkonnas kasutatavaid litsentse, ühtlustatud litsentsiks on CC BY-SA 3.0. Seega on nüüdsest võimalik materjali avalikuks muuta vaid juhul, kui nõustud nimetatud litsentsiga (litsents ei kehti lingitud sisule, mis asub keskkonnast väljas). Selleks ava allolevast nupust materjali muutmise vaade ja anna nõusolek litsentsile. (RU)');
call insert_translation('MATERIAL_IS_PRIVATE', 'Materjal on muudetud privaatseks', 'Material has been made private');
call insert_translation('PORTFOLIO_LICENSE_WARNING', 'Kuna kogumikul puudub CC BY-SA 3.0 litsents või sellega kokkusobiv litsents (CC BY 3.0), ei saa kogumikku avalikuks muuta. E-koolikott ühtlustab keskkonnas kasutatavaid litsentse, ühtlustatud litsentsiks on CC BY-SA 3.0. Seega on nüüdsest võimalik kogumik avalikuks muuta vaid juhul, kui nõustud nimetatud litsentsiga (litsents ei kehti lingitud sisule, mis asub keskkonnast väljas). Selleks ava allolevast nupust kogumiku muutmise vaade ja anna nõusolek litsentsile.',
 'Since this portfolio is missing the CC BY-SA 3.0 or the CC BY 3.0 license, you cannot make it public. E-koolikott is consolidating the licenses being used on the website. The new license will be CC BY-SA 3.0. From this point onward, you can only make your portfolio public, if you agree with the aforementioned license (the license does not apply to external links). To do that open the portfolio editor and add the new license to your material.');
call insert_translation('PORTFOLIO_PUBLICATION_TITLE', 'Kogumiku avalikustamine', 'Portfolio publication');

SET foreign_key_checks = 1;