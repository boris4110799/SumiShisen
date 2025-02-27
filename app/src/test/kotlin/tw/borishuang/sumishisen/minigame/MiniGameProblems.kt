package tw.borishuang.sumishisen.minigame

object MiniGameProblems {
    val problemList = listOf(
        "aabacdefeghhizfgeajdjklfmdinoocldnkcoikclgpiblnhphpbmmpkjzefjbnmog",
        "aabcdefzaxgehijkhlmmbmbelneoffzkfonddcomhhajgxccbizkkligjgnojndzil",
        "aabczdedfzzgbhicjkljbmadannihnflihjxojlmgkmezlemdkceiohggbnfofcozk",
        "aazbcdcefghijkfhazlxmbgbnlfcjgacemoiibglxelzhkzmfdnodomkkoejinnjdh",
        "abacddecxzfgczxahigjklidlbfimhzdkkifmfmhnobeexahmloneonjkobnljcgjg",
        "abacdefghagdxiffjkjzclkdhxmcinmmognheneanhbijdbgcjkzmolxelbfoklozi",
        "abacdefgzhcxgcidjebkdlhxmnfmzliozxaoejmigcimnfbdgjbknhoekjlknfaloh",
        "abaczdefdbgcbxgzdhcijjkezlfeheizmljnfangklomoighdalfnoijckmmnkxboh",
        "abbcdefagghcijklhmigzkmjanfijehnkjxcodbnfkfdmmaiobloexdgnelohzxclz",
        "abbcdefbzghiidjhkcfhkgdxjzkxabjlfmecminjgxzoelnnnfoolahalkemgdicom",
        "abbczdbzcefghdijahfjckcglmnfkiliaiedazzngleongmemzkojojmfhxblodhnk",
        "abcadefbgchixdfjekkakdlbmzgmhfzmlnjmhixalnoghoncezjjzdniokibeolfgc",
        "abcadefbghfigcfcjkiejzhlibmmnmocneapdozfinlpkdbnajojkdhhlempplggok",
        "abcadefefbgghijklkzjgmlzjhfzdaekcnzgobmldiocmhoeohidbijzlkcanxnfmn",
        "abcadefgzhizgcbjzzkfdelxemalifnhncjkgjbooixamolbfhoidkhnklecjmdnmg",
        "abcadxecfghijikkdxlifmizkaxmlnkonggchejhmldojbanfezzfohnmlbejcdbgo",
        "abcazccdebfgfhzhcigzdfjbklajimhleklmfinlgmndonxxnzeokhigjdjaoebmko",
        "abcbddefgehxgzgghijckfjlfmxbmeneilmohfoccxnhdozaanomlkknzjiajidkbl",
        "abcbdefdghhgzijgkdfledhzmnnmnkjoxellzkcxxlmmbeafkiihncgcofojbiaoja",
        "abcbdefgzfhijeczgjkcdbxzalfedfijlzchkamnlimnomoblhkakgmjgindneozho",
        "abcccdefghhizjbkcbbeifgllmznodfmmonhjzdnllheeakgjjigminokdkfaxaxox",
        "abcdabefegzhichjhzklilmnhkdggokfibzfeocljxamjmmidejofcdloxknnzbang",
        "abcdabefgehigjikxjlkmzdznhobbmookaamcdmgneiljfhckhjozezldgcnnlifxf",
        "abcdaeafghijklegleblemzhmcinjhonfogmlnififapjdcjpbodcpkzbpdnhgmkok",
        "abcdaefghijcgfekilikmnmfdnlzhccxbohixbedzazomedkzjnbkmjhogaglnfloj",
        "abcdaefzghgiejekfjbkallhbmcdncakdmgmooihimdlnljfznxfozbzjnieohzgck",
        "abcdbaxefczgbhhijfekkgdlfmnocmhibxnhmezodkoizclgafigjlenkoldanxmjj",
        "abcdbdefghzzxegcgzizjehkilbcmldkdjaibhickmxnlfjhnnonloaeommkajffog",
        "abcdbefbgachixhzxajkfhillmncekkncoggbijhmoonlgankfemddfmzoxidlejzj",
        "abcdbefcghczcijekjfklkahilmnjzdxbemlojedgbnmdzanohiomkioxglfanfzgh",
        "abcdbefgehizejjhdifkzdlfmznflaocolhobgdccnoimgxjjmkiaxmhkakgnbezln",
        "abcdceaafgxhijkdxlggbljcmkzafzzlnimzoocdijoomkednleihmjfknbgenhbhf",
        "abcdcezfghiajklemndobdmniooxlnacakigzcinhdmojhxgkfefglezmjlkbfzjhb",
        "abcddaebfghiijgfhzkzljzcmegclnenjjbendamlhmfkcnaobogkolmdfziikhoxx",
        "abcddaefezgahifigajzkijglmegkcknojiblfpomnpbollmohdpdhpmnfbchekjnc",
        "abcddefagehbijcafkilkfhcdmzkblagxnjfnxomoignhihjdmknezmlbjozxgcoel",
        "abcddefxghijizekhcdelxlgfjkbblgfmnolzhcmakomafhngjkiioeazjcndzbonm",
        "abcdeabcdbfgahgiejeklzzhmgzkxlchzamcnjgjilknxlbjhfiomdomnnffeidoko",
        "abcdeaefghbijkdcjghhlmchgkazjlnenobkjkzfxdzlfbofalgocoiinnizmmdemx",
        "abcdeafgezbhijkflcmnogdizakkmhhglnmkoiopamjpdenbnfchoplfcjjeidgplb",
        "abcdeafgghijbgcbazklzmdixncidjiafnmhkeohhzolbemmflfonkgezzoncjkjld",
        "abcdeafgzhijehdkhflfacmaknodxiljhozxognmiogxcnzlnbckfkeijmbejbdglm",
        "abcdeazbzfghidfecjbkzlcmjbgnhoeligfiakmxdoohmfgkkinmjzanjlxonclhde",
        "abcdebfaghcifjeigajzkelmnmjmnkodbxhilkzmodezfohnlkfhgczidcxnjbagol",
        "abcdebfghghgaeijkklminiofodnefkbnoimhlcmlpnejjjchckloazpbpdpafdzgm",
        "abcdebfghizdjzjkalhmifhegnjmfekohozialbmngcczmlaokfixdecdjkbxoglnn",
        "abcdebzfzfgbhijkilfmijgjczhdzedkalmaexnglnnkfhoocmndhlzeaockmjbiog",
        "abcdecfxzzzdghxzgacihfjhklxdalhmjfnenbmmiggjekbjxzmcanilebkxnkflid",
        "abcdedffagzahhibccfjckkligxgelgmknbmjbhlxnimekjnhadizljznzxxzdxefm",
        "abcdedfghcijhaklkhedmdnfjnbcxgocalfzmjknbfmeiogokxholxeaizbizngjlm",
        "abcdedfghibcjkfjzlmlfxjnozkakgeclaeelfnxinhidooajbkcdhhzgozbmgmmni",
        "abcdefbbghiziajkjflmxegjadnfjhzkioelmlhfkezadnncxmgcohbkdoncgzoilm",
        "abcdefbgechijkhlmcbzcahgijenflodfpldkpiznmjblopamdmnnoakjpfkgioehg",
        "abcdefbghicjkjjlamdgmbebgdzznoohlzeikohmjkacfhgnciizkldonafxnfelzm",
        "abcdefbzghijedagkiljchlmnnadhlehzoiajegkcondxcokgmfzlifxbkxmbjfonm",
        "abcdefcfefghijkclgmnekdhzbodlapbzelndlmjmnjbojpfgiaahgcinohmopikkp",
        "abcdefcghcidxjfixdehklmjjaizdbnfonbogzmoemgakkziklhagennflobmxhljc",
        "abcdefcghijjbcfazklximihznggolbbkflhikenzademdmmojanzcfglhokejodnx",
        "abcdefcghijklmgjlnodzdocezbhmaifochpfohlmnpmgbkngpeiipklandbjkeafj",
        "abcdefdaceghbijfklimnclabmaohkppnhmedjklzbjpgnggedijfmkozlofpnihco",
        "abcdefeeghijcfafklxjbdhiilmmkknegzgolabjxocgnoxhnbihmnacdjozzmdlkf",
        "abcdeffghijeklimzdjgginjnjigofbldaomzxhhflxlcmkzbbheoaadkemcocnnkx",
        "abcdefgabehijakckclmmlbnjdhfeomgdgomjpnpkolnzhfajignfdcilbhpkiozep",
        "abcdefgbhizacjikelcmjfnikixcdozngzfmlnlzgdlhhoekbemzoaomkdajjfnbhg",
        "abcdefgebhijjakzlmczixnkngecflidbbgodmakcnjmolielhdghfzmhaoofnkxjx",
        "abcdefgehijzeklmnfkoiflmzdibmnxgohnlhjxjgdahdjbeagczafomoklbcncxki",
        "abcdefgghxijkdfgljfhhekdiambgjnmbokxznoodnzllfocjlazncimkbhmacexie",
        "abcdefghficxcjzkzghjklmcdabfdknjnikbezfmnlnohadgoizomhlbeogmaixlje",
        "abcdefghfihbjkldkmnkmkfccnzznzmandgxaflahhobbeojogicjxgixdloemejil",
        "abcdefghgdijkihlbbxxmcgnjlcfzzdnmaifhzkixejoamjlbcgolmkknaofoehden",
        "abcdefghgifbzhjkelmmelnxkemdcdnzalnjbbokgmiioghofkjzaalihocjzndfcz",
        "abcdefghhigjikfklebzmkcknlnlhzagiaoolfcjndnbfmoxhdzajjoxgeimzbemdc",
        "abcdefghiabcjjkeddzljmgfckanojklolbgihmdmilahiefgnkmbohexncxzxnfzo",
        "abcdefghicjkdlmcakfmeimklhfndjjngzadoxbkfhoznbxbgzjlihnogazceilemo",
        "abcdefghidjikflzzzjeabzigfzmcanmlnokodmhmfcdohlhgnajnbkjlekoxcgebi",
        "abcdefghijiadiacaekxlmnogcnomznfhjlzkedcdlbffjbmzghxeoigohmnkbkjxl",
        "abcdefghijzakieljxmdcnjnckfhikmozgjbloiadnhfakmcozebbgdgomexnlfzlh",
        "abcdefghijzkdflkbmkambdijehxeahdnncfnzcnzikcgaoximfgljoeoobglhlzmj",
        "abcdefghixjdiegiklmzezbnjbkklfhecnmogjchdkagzbazffoljlhzamiodnnmoc",
        "abcdefxeczfghaxdgzigbgjkcalmjnlxoholeidinifzackbmlehkhkmjnfdboonmj",
        "abcdefzbbagchgxijklxlzxlmekcgnndjbjahgmoikfddhonhmlmnkeeajoiifocfz",
        "abcdefzcgbdhaixczedajjiklmmdgxienbfkmegomlblhangfhzjkocxfjhlkoinno",
        "abcdefzghijjgzxefckdghilmalzzmjhgxcmahinceobonkdkebjfooinlndkambfl",
        "abcdexefghiixjfckilzlgmmbbanbozmlghozdncjedfokodkgjiaafnkmjhnzchle",
        "abcdexfgdhijcfkhlazjbzzjljhmknmbmhongoinelnogbclgeckixzoaeamfikfdd",
        "abcdexfzghijihcekzlfmanbfzdjklebdhxonmaniekhcfbonjaclgoogdkgzljmmi",
        "abcdexxfcghhaijklfmlnfgiamzmnojczdeknkhjbziieadoogjkmdzhcfnlgoebbl",
        "abcdexzcfdghexcijkfigjelzhjhlemcbdbighnonglbdfzonikafjamnkakolmzom",
        "abcdezeffacghdxxfijzfkbhljjmeminakdooglgkenbhmoizkxalghonbmlnjicdc",
        "abcdezfaghicgijjklhmeekinmfbjhagomlozldxhnfdckmgoxniozbebkdlfjczna",
        "abcdezfeghifjkjlmbflhdzmcijnzomadklhxeebficgkkgczbnlmoondijaagohzn",
        "abcdezfghixjklimkmfcdilmgnodnhaaikfbjczkfbdzocxojnanbglhgjoxleemeh",
        "abcdezfxghfgibjeklmijimfaemgizkcdnabocdfhzkcblhdmxngjhznjleokoonal",
        "abcdxefghicfifcjkdlmlzcfbedbgjnogdjnezlmhkiahnikozgmxoaoaeblhmkjxn",
        "abcdzebfgxhifjbfkhjelzlicdmkzngzjmodeknochzdgociakmflmanajlobniehg",
        "abcdzeefghcijfkekcladmbkmnzjoffzdmdxomogajninekolnxcblzbagilijhhgh",
        "abcdzefbagxheigjzkaghflkzdmnjjnmnjmnhaodeikoboldbegxcfmicihfokllzc",
        "abczdeecfghdhijkzlaibgcixxegmklhnadjfbhmojjloikdxkzelnoafombmncfng",
        "abczdefgabhgcizdjkflimnioknnhpmapjjdmokgfcldokbhmiechbppjlanfeoegl",
        "abxcadebfghijickzidzleajzxbkcgjmlnfhgkonohbcgkmaimjddnfzelneolfmoh",
        "abzcdaefxghdziccgjfkjljifmgziknkxoclgaoemblnadeodjehbinklmfhnhzbom",
        "abzcdefghidjklhmcjdxzalnmikzgonlebmgleobzcoknohcxjdahfjmgfiniefkba",
        "abzcdefghijzhkalxmidkbxmkbnhmgzjfoioflenfocxkegbglamhjnjdnlidcoeac",
        "abzcdexfafbzgfhbihgjdgzdkkalmkmngmnodkoblonfxmceleacihljhcnxeojiji",
        "abzcdzefghibczejibkkgflihfmmjnojkeoiodldzngjadaonhxkmazlmlgefhnccb",
        "axbbccdeafghibjxbicekljefkgxzdmlkahjnmfzjzoddgolhfekhinmlocnaimgno",
        "axbcdedzfcgxhijgxahiiafcbkleljmjdmnahklfkoonnggefnihbzbljmoedmzcko",
        "axbcdezffghijgklmekafbmznaehbnjjdmhxkladnbcflgxoozjghcciikdinmeolo",
        "azbacdefzbzbccgedexbhijklmfzfljhhfdkinlogahginookmdjomncalmijnkgze",
        "azbacdzbbefghbijkdhalmmgnefohzxjimaglxkecljlxooeiihjcnnnogfcfkdmdk",
        "azbcdbefgghiijkcjlmxfcfmdxbjnmfnigohzlkonckmgeodiaekhzoaebljnlzhda",
        "azbcdcefdgheixjkfhjjlaxemnhjhalfcgixkdicmzodnmgznlkgbeabkooolifnbm",
        "azbcdecfgchhddiijklgjfemxchnlaozezagffajonhixbngenkolzomjmkbkbdiml",
        "azzbcbdefgbdhijfkkfzkjlmdfaxnmxoazhbmechenooncikjgcimngioalljledhg",
        "xaabacdefghijjkzlimhmnxgfoafobkzhmgnjeeedlniddcbnhgklzlobfcicjzokm",
        "xabcdefcggehfibehjhbcklxmecnnohmlagobfdljjzijoiaokzzdzkmnlndigfmak",
        "xabcdefgzhhdeichcgjkdijakjlmcehfenxoazfkgdnbibllmoxmafgnonbmkljozi",
        "zaabcdefghgijdklzjebmdkniclzfomxailbkmocfnmkzdelejocijoghhgxnanhbf",
        "zabcdefghijkllkcbmgdnjbflopnoakbfgnlmezfkopahpiijnigpmmchohdjeedca",
        "zabzcdefgehiabzfjahiidkcflmhenmnxkohcdmneoaoxojgjlgdklmlnbcjxkfgib",
    )
}