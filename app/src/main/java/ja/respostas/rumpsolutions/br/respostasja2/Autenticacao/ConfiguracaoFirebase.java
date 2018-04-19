package ja.respostas.rumpsolutions.br.respostasja2.Autenticacao;

import android.provider.ContactsContract;
import com.google.firebase.auth.FirebaseAuth;

public final class ConfiguracaoFirebase {
//autenticacao manual do firebase onde o Mauth virou autenticacao nos codigos


        private static FirebaseAuth autenticacao;


        public static FirebaseAuth getFirebaseAutenticacao(){
            if (autenticacao == null){
                autenticacao = FirebaseAuth.getInstance();

            }
            return autenticacao;
        }

}
