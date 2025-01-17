package pumlFromJava.typeDiagramme;

import pumlFromJava.classes.Attributs;
import pumlFromJava.classes.Constructeurs;
import pumlFromJava.classes.Methodes;
import pumlFromJava.liens.Associations;
import pumlFromJava.liens.Heritages;
import pumlFromJava.liens.Interfaces;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DCA
{
    public DCA(Element element, FileWriter myWriter)
    {
        this.element=element;
        this.myWriter=myWriter;
    }
    Element element;
    FileWriter myWriter;

    //La méthode qui créer le DCA
    public void creerDCA()
    {
        try
        {
            //L'en-tête du fichier
            myWriter.write("@startuml\n");
            myWriter.write("skinparam classAttributeIconSize 0\n"+
                    "skinparam classFontStyle Bold\n"+
                    "skinparam style strictuml\n"+
                    "hide empty members\n");

            //Par contre, comme on écrit directement le nom du package, pour l'instant notre Doclet ne marche que si on lui fournit un et un seul package.
            //Nom du package
            myWriter.write("package "+element.getSimpleName().toString()+"\n{\n");

            //Pour récupérer les liens pour les héritages, associations et interfaces
            Heritages recupHeritage = new Heritages(element);
            Associations recupAssociations = new Associations(element);
            Interfaces recupInterfaces = new Interfaces(element);
            ArrayList<String> heritages = recupHeritage.obtenirLesHeritages();
            ArrayList<String> associations = recupAssociations.obtenirLesAssociations();
            ArrayList<String> interfaces = recupInterfaces.obtenirLesImplements();

            //Chaque élément présent dans le package
            for(Element e:element.getEnclosedElements())
            {
                //Le type et le nom
                myWriter.write(e.getKind() + " " + e.getSimpleName());

                //Si c'est une énumération ou une interface
                if (e.getKind() == ElementKind.INTERFACE)
                {
                    //J'ajoute le stéréotype <<interface>>
                    myWriter.write("<<interface>>\n{\n");
                }
                else if (e.getKind() == ElementKind.ENUM)
                {
                    //Ou <<énumération>>
                    myWriter.write("<<énumération>>\n{\n");
                }
                else
                {
                    myWriter.write("\n{\n");
                }

                //Pour chaque élément dans cet élément (donc chaque élément dans la classe/énumération ou interface)
                for (Element el : e.getEnclosedElements())
                {
                    //Je récupère les attributs
                    Attributs recupAttribut = new Attributs(el);
                    ArrayList<String> attributs = recupAttribut.obtenirLesAttributs();

                    //J'écris
                    for (String s : attributs)
                        myWriter.write(s);

                    //Je suis dans un DCA, donc pas besoin des méthodes et constructeurs
                }
                myWriter.write("\n}\n");
            }

            //Ecriture des liens pour les interfaces, héritages et associations
            for (String s : interfaces)
                myWriter.write("\n" + s + "\n");

            for (String s : heritages)
                myWriter.write("\n" + s + "\n");

            for (String s : associations)
                myWriter.write("\n" + s + "\n");

            //Fin du fichier
            myWriter.write("}\n@enduml");
        }
        catch (IOException e)
        {
            System.out.println("Erreur d'écriture.");
            e.printStackTrace();
        }
    }
}
